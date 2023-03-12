package com.sp.fn.adsync.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sp.fn.adsync.service.AdUsersSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionContext {

  private static final Logger log = LoggerFactory.getLogger(ExecutionContext.class);

  private AdUsersSyncService adUsersSyncService;

  public ExecutionContext(Config config) {
    log.info("Loading configuration");
    try {
      Injector injector = Guice.createInjector(new Services(config));
      this.adUsersSyncService = injector.getInstance(AdUsersSyncService.class);
    } catch (Exception e) {
      log.error("Could not start", e);
    }
  }

  public AdUsersSyncService getAdUsersSyncService() {
    return adUsersSyncService;
  }

}
