package com.sp.fn.adsync;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sp.fn.adsync.config.Config;
import com.sp.fn.adsync.config.ExecutionContext;
import com.sp.fn.adsync.config.HibernateConfig;
import com.sp.fn.adsync.config.UnixTimestampAdapter;
import com.sp.fn.adsync.entity.AdSyncAudit;
import com.sp.fn.adsync.service.AdUsersSyncService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.webcompere.lightweightconfig.ConfigLoader;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestStreamHandler {

  private static final Logger log = LoggerFactory.getLogger(App.class);
  private Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new UnixTimestampAdapter())
      .create();
  private Config config = ConfigLoader.loadYmlConfigFromResource("configuration.yml", Config.class);
  private ExecutionContext executionContext = new ExecutionContext(config);
  private SessionFactory sessionFactory = HibernateConfig.createSessionFactory(config);

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)))) {
      Event event = gson.fromJson(reader, Event.class);
      AdUsersSyncService adUsersSyncService = executionContext.getAdUsersSyncService();
      AdSyncAudit adSyncAudit = adUsersSyncService.syncUsers(sessionFactory, event);
      log.info("AD Sync Finished: {}", adSyncAudit);
      writer.write(gson.toJson(adSyncAudit));
    } catch (Throwable e) {
      log.error("Ad Sync Failed", e);
    } finally {
      HibernateConfig.flushConnectionPool(sessionFactory);
    }
  }
}
