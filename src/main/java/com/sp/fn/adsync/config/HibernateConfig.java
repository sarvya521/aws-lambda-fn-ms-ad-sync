package com.sp.fn.adsync.config;

import static org.hibernate.cfg.AvailableSettings.BATCH_VERSIONED_DATA;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_SCHEMA;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.JDBC_TIME_ZONE;
import static org.hibernate.cfg.AvailableSettings.ORDER_INSERTS;
import static org.hibernate.cfg.AvailableSettings.ORDER_UPDATES;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.STORAGE_ENGINE;
import static org.hibernate.cfg.AvailableSettings.URL;
import static org.hibernate.cfg.AvailableSettings.USER;

import com.sp.fn.adsync.entity.AdSyncAudit;
import com.sp.fn.adsync.entity.ExternalAccountType;
import com.sp.fn.adsync.entity.Role;
import com.sp.fn.adsync.entity.User;
import com.sp.fn.adsync.entity.UserAudit;
import com.sp.fn.adsync.entity.UserExtra;
import com.sp.fn.adsync.entity.UserExtraAudit;
import com.sp.fn.adsync.entity.UserRole;
import com.sp.fn.adsync.entity.UserRoleAudit;
import com.sp.fn.adsync.entity.UserStatus;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public final class HibernateConfig {

  private HibernateConfig() {
    throw new AssertionError();
  }

  public static SessionFactory createSessionFactory(Config config) {
    Map<String, String> settings = new HashMap<>();
    settings.put(URL, config.getDatasource().getUrl());
    settings.put(USER, config.getDatasource().getUsername());
    settings.put(PASS, config.getDatasource().getPassword());
    settings.put(DEFAULT_SCHEMA, "spdb");
    settings.put(DIALECT, "org.hibernate.dialect.MySQL8Dialect");
    settings.put(STORAGE_ENGINE, "innodb");
    settings.put(DRIVER, "com.mysql.cj.jdbc.Driver");
    settings.put(STATEMENT_BATCH_SIZE, String.valueOf(config.getDatasource().getBatchSize()));
    settings.put(ORDER_INSERTS, "true");
    settings.put(ORDER_UPDATES, "true");
    settings.put(BATCH_VERSIONED_DATA, "true");
    settings.put(JDBC_TIME_ZONE, "UTC");
    settings.put("hibernate.hikari.minimumIdle", "1");
    settings.put("hibernate.hikari.maximumPoolSize", "2");
    settings.put("hibernate.hikari.idleTimeout", "30000");
    settings.put("hibernate.hikari.connectionTimeout", "20000");

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySettings(settings)
        .build();

    return new MetadataSources(registry)
        .addAnnotatedClass(AdSyncAudit.class)
        .addAnnotatedClass(ExternalAccountType.class)
        .addAnnotatedClass(Role.class)
        .addAnnotatedClass(User.class)
        .addAnnotatedClass(UserAudit.class)
        .addAnnotatedClass(UserExtra.class)
        .addAnnotatedClass(UserExtraAudit.class)
        .addAnnotatedClass(UserRole.class)
        .addAnnotatedClass(UserRoleAudit.class)
        .addAnnotatedClass(UserStatus.class)
        .buildMetadata()
        .buildSessionFactory();
  }

  public static void flushConnectionPool(SessionFactory sessionFactory) {
    ConnectionProvider connectionProvider = sessionFactory.getSessionFactoryOptions()
        .getServiceRegistry()
        .getService(ConnectionProvider.class);
    HikariDataSource hikariDataSource = connectionProvider.unwrap(HikariDataSource.class);
    hikariDataSource.getHikariPoolMXBean().softEvictConnections();
  }

}
