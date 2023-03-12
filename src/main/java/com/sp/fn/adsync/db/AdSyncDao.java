package com.sp.fn.adsync.db;

import com.sp.fn.adsync.api.SpUser;
import com.sp.fn.adsync.entity.AdSyncAudit;
import com.sp.fn.adsync.entity.User;
import com.sp.fn.adsync.entity.UserAudit;
import com.sp.fn.adsync.entity.UserExtra;
import com.sp.fn.adsync.entity.UserExtraAudit;
import com.sp.fn.adsync.entity.UserRole;
import com.sp.fn.adsync.entity.UserRoleAudit;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.hibernate.Session;
import org.hibernate.Transaction;

public interface AdSyncDao {

  default UUID findRoleIdByName(Session session, String name) {
    return session.createQuery("SELECT id FROM Role WHERE name = :name", UUID.class)
        .setParameter("name", name)
        .getSingleResult();
  }

  default Optional<Instant> findLastSyncTimeByOrgId(Session session, UUID orgId) {
    return Optional.ofNullable(
        session.createQuery("SELECT MAX(ts) FROM AdSyncAudit WHERE orgId = :orgId",
                Instant.class)
            .setParameter("orgId", orgId)
            .getSingleResult());
  }

  default List<User> findUsersByEmailIn(Session session, Set<String> emails) {
    return session.createQuery("FROM User WHERE email IN(:emails)", User.class)
        .setParameter("emails", emails)
        .getResultList();
  }

  default void updateAllUsers(Session session, int batchSize, List<SpUser> users) {
    Transaction transaction = session.beginTransaction();
    IntStream.range(0, users.size())
        .forEach(i -> {
          if (i > 0 && i % batchSize == 0) {
            session.flush();
            session.clear();
          }
          SpUser spUser = users.get(i);
          User user = spUser.getUser();
          session.merge(user);
          session.persist(new UserAudit(user));
          UserExtra userExtra = Optional.ofNullable(session.get(UserExtra.class, user.getId()))
              .orElseGet(() -> new UserExtra(user.getId()));
          userExtra.setExternalUserId(spUser.getExternalUserId());
          userExtra.setExternalAccountType(spUser.getExternalAccountType());
          session.persist(userExtra);
          session.persist(new UserExtraAudit(userExtra));
        });
    transaction.commit();
  }

  default void saveAllUsers(Session session, int batchSize, List<SpUser> users) {
    Transaction transaction = session.beginTransaction();
    IntStream.range(0, users.size())
        .forEach(i -> {
          if (i > 0 && i % batchSize == 0) {
            session.flush();
            session.clear();
          }
          SpUser spUser = users.get(i);
          User user = spUser.getUser();
          session.persist(user);
          session.persist(new UserAudit(user));
          UserRole userRole = new UserRole(user.getId(), spUser.getRoleId(), true);
          session.persist(userRole);
          session.persist(new UserRoleAudit(userRole));
          UserExtra userExtra = new UserExtra(user.getId(), spUser.getExternalUserId(),
              spUser.getExternalAccountType());
          session.persist(userExtra);
          session.persist(new UserExtraAudit(userExtra));
        });
    transaction.commit();
  }

  default Long auditAdSync(Session session, AdSyncAudit adSyncAudit) {
    Transaction transaction = session.beginTransaction();
    Long id = (Long) session.save(adSyncAudit);
    transaction.commit();
    return id;
  }
}
