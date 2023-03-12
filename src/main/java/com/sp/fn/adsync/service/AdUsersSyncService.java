package com.sp.fn.adsync.service;

import com.sp.fn.adsync.Event;
import com.sp.fn.adsync.api.AdAuthApi;
import com.sp.fn.adsync.api.AdUser;
import com.sp.fn.adsync.api.ListAdUsersApi;
import com.sp.fn.adsync.api.ListAdUsersApiResponse;
import com.sp.fn.adsync.api.SpUser;
import com.sp.fn.adsync.config.Config;
import com.sp.fn.adsync.db.AdSyncDao;
import com.sp.fn.adsync.entity.AdSyncAudit;
import com.sp.fn.adsync.entity.ExternalAccountType;
import com.sp.fn.adsync.entity.User;
import com.sp.fn.adsync.entity.UserStatus;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdUsersSyncService {

  private static final Logger log = LoggerFactory.getLogger(AdUsersSyncService.class);
  private static final String GRANT_TYPE = "client_credentials";
  private static final String SCOPE = "https://graph.microsoft.com/.default";
  private final Config config;
  private final AdAuthApi adAuthApi;
  private final ListAdUsersApi listAdUsersApi;
  private final AdSyncDao dao;

  @Inject
  public AdUsersSyncService(Config config, AdAuthApi adAuthApi, ListAdUsersApi listAdUsersApi,
      AdSyncDao dao) {
    this.config = config;
    this.adAuthApi = adAuthApi;
    this.listAdUsersApi = listAdUsersApi;
    this.dao = dao;
  }

  private String getAccessToken(Event event) throws URISyntaxException {
    String adTokenEndpoint = MessageFormat.format(config.getAdTokenEndpoint(), event.getTenantId());
    String clientId = event.getClientId().toString();
    String clientSecret = event.getClientSecret();
    return "Bearer " + adAuthApi.getAccessToken(
            new URI(adTokenEndpoint),
            GRANT_TYPE, SCOPE, clientId, clientSecret)
        .getToken();
  }

  private Set<AdUser> loadUsersCreatedToday(Instant loadUsersFromTs, String accessToken)
      throws URISyntaxException {
    String url = MessageFormat.format("{0}?$select={1}&$count=true&$filter={2}",
        config.getListUsersEndpoint(),
        URLEncoder.encode("id,givenName,surname,accountEnabled,mail,deletedDateTime",
            StandardCharsets.UTF_8),
        URLEncoder.encode("createdDateTime ge " + loadUsersFromTs.toString()
                + " and mail ne null and givenName ne null and surname ne null",
            StandardCharsets.UTF_8));
    Set<AdUser> users = new HashSet<>();
    int totalUsersCreatedToday = 0;
    while (Objects.nonNull(url)) {
      URI uri = new URI(url);
      ListAdUsersApiResponse listAdUsersApiResponse = listAdUsersApi.get(uri, accessToken);
      url = listAdUsersApiResponse.getNextLink();
      if (totalUsersCreatedToday == 0) {
        totalUsersCreatedToday = listAdUsersApiResponse.getCount();
        log.info("total {} users found created today", totalUsersCreatedToday);
      }
      users.addAll(listAdUsersApiResponse.getUsers());
    }
    return users;
  }

  private Set<AdUser> loadUsersDeletedToday(Instant loadUsersFromTs, String accessToken)
      throws URISyntaxException {
    String url = MessageFormat.format("{0}?$select={1}&$count=true&$filter={2}",
        config.getListUsersEndpoint(),
        URLEncoder.encode("id,givenName,surname,accountEnabled,mail,deletedDateTime",
            StandardCharsets.UTF_8),
        URLEncoder.encode("deletedDateTime ge " + loadUsersFromTs.toString()
                + " and mail ne null and givenName ne null and surname ne null",
            StandardCharsets.UTF_8));
    Set<AdUser> users = new HashSet<>();
    int totalUsersDeletedToday = 0;
    while (Objects.nonNull(url)) {
      URI uri = new URI(url);
      ListAdUsersApiResponse listAdUsersApiResponse = listAdUsersApi.get(uri, accessToken);
      url = listAdUsersApiResponse.getNextLink();
      if (totalUsersDeletedToday == 0) {
        totalUsersDeletedToday = listAdUsersApiResponse.getCount();
        log.info("total {} users found deleted today", totalUsersDeletedToday);
      }
      users.addAll(listAdUsersApiResponse.getUsers());
    }
    return users;
  }

  private void mapToSpUsers(UUID orgId, UUID roleId, Session session,
      Set<AdUser> adUsers, List<SpUser> usersToCreate, List<SpUser> usersToUpdate) {
    Set<String> emails = adUsers.stream()
        .map(AdUser::getMail)
        .collect(Collectors.toSet());
    List<User> existingUsers = dao.findUsersByEmailIn(session, emails);
    Map<String, User> existingSpUsers = existingUsers.stream()
        .collect(Collectors.toMap(User::getEmail, Function.identity()));

    adUsers.stream()
        .filter(adUser -> !existingSpUsers.containsKey(adUser.getMail()))
        .map(adUser -> {
          User user = new User(adUser, orgId, UserStatus.ACTIVE);
          if (!adUser.isAccountEnabled()) {
            user.setStatus(UserStatus.INACTIVE);
          }
          return new SpUser(
              user,
              adUser.getId().toString(),
              roleId,
              ExternalAccountType.MICROSOFT);
        })
        .collect(Collectors.toCollection(() -> usersToCreate));

    adUsers.stream()
        .filter(adUser -> existingSpUsers.containsKey(adUser.getMail()))
        .map(adUser -> {
          User user = existingSpUsers.get(adUser.getMail());
          if (!adUser.isAccountEnabled()) {
            user.setStatus(UserStatus.INACTIVE);
          }
          return new SpUser(
              user,
              adUser.getId().toString(),
              roleId,
              ExternalAccountType.MICROSOFT);
        })
        .collect(Collectors.toCollection(() -> usersToUpdate));
  }

  public AdSyncAudit syncUsers(SessionFactory sessionFactory, Event event)
      throws ExecutionException, InterruptedException, URISyntaxException {
    UUID orgId = event.getOrgId();
    boolean isFirstSync = false;
    Instant loadUsersFromTs;
    try (Session session = sessionFactory.openSession()) {
      Optional<Instant> lastSyncTimeByOrgId = dao.findLastSyncTimeByOrgId(session, orgId);
      if (lastSyncTimeByOrgId.isEmpty()) {
        log.warn("No previous AD Sync found for org {}", orgId);
        loadUsersFromTs = Instant.parse("1970-01-01T00:00:00Z");
        isFirstSync = true;
      } else {
        loadUsersFromTs = lastSyncTimeByOrgId.get();
      }
    }
    String accessToken = getAccessToken(event);
    Instant now = Instant.now();
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<Set<AdUser>> createdUsersFuture = executorService.submit(
        () -> loadUsersCreatedToday(loadUsersFromTs, accessToken));
    Future<Set<AdUser>> deletedUsersFuture = null;
    if (!isFirstSync) {
      deletedUsersFuture = executorService.submit(
          () -> loadUsersDeletedToday(loadUsersFromTs, accessToken));
    }
    executorService.shutdown();
    try (Session session = sessionFactory.openSession()) {
      List<SpUser> usersToCreate = new ArrayList<>();
      List<SpUser> usersToUpdate = new ArrayList<>();

      UUID roleId = dao.findRoleIdByName(session, "USER");

      Set<AdUser> newUsers = createdUsersFuture.get();
      if (!newUsers.isEmpty()) {
        mapToSpUsers(orgId, roleId, session, newUsers, usersToCreate, usersToUpdate);
      }

      Set<AdUser> deletedUsers = Collections.emptySet();
      if (!isFirstSync) {
        deletedUsers = deletedUsersFuture.get();
      }
      if (!deletedUsers.isEmpty()) {
        mapToSpUsers(orgId, roleId, session, deletedUsers, usersToCreate, usersToUpdate);
      }

      log.info("total {} users to be created", usersToCreate.size());
      log.info("total {} users to be updated", usersToUpdate.size());

      if (!usersToCreate.isEmpty()) {
        dao.saveAllUsers(session, config.getDatasource().getBatchSize(), usersToCreate);
      }
      if (!usersToUpdate.isEmpty()) {
        dao.updateAllUsers(session, config.getDatasource().getBatchSize(), usersToUpdate);
      }

      Long totalUsersRemoved = usersToUpdate.stream()
          .map(spUser -> spUser.getUser().getStatus())
          .filter(userStatus -> userStatus != UserStatus.ACTIVE)
          .count();
      AdSyncAudit adSyncAudit = new AdSyncAudit(
          orgId,
          usersToCreate.size(),
          totalUsersRemoved.intValue(),
          true,
          null,
          now);
      dao.auditAdSync(session, adSyncAudit);
      return adSyncAudit;
    }
  }
}
