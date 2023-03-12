package com.sp.fn.adsync.config;

import static feign.Logger.Level.FULL;

import com.google.inject.AbstractModule;
import com.sp.fn.adsync.api.AdAuthApi;
import com.sp.fn.adsync.api.AdAuthApiResponseGsonDecoder;
import com.sp.fn.adsync.api.ListAdUsersApi;
import com.sp.fn.adsync.api.ListAdUsersApiResponseGsonDecoder;
import com.sp.fn.adsync.db.AdSyncDao;
import feign.Feign;
import feign.form.FormEncoder;
import feign.slf4j.Slf4jLogger;

public class Services extends AbstractModule {

  private final Config config;

  public Services(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    AdAuthApi adAuthApi = Feign.builder()
        .encoder(new FormEncoder())
        .decoder(new AdAuthApiResponseGsonDecoder())
        .logger(new Slf4jLogger())
        .logLevel(FULL)
        .target(AdAuthApi.class, config.getAdTokenEndpoint());

    ListAdUsersApi listAdUsersApi = Feign.builder()
        .decoder(new ListAdUsersApiResponseGsonDecoder())
        .logger(new Slf4jLogger())
        .logLevel(FULL)
        .target(ListAdUsersApi.class, config.getListUsersEndpoint());

    bind(Config.class).toInstance(config);
    bind(AdAuthApi.class).toInstance(adAuthApi);
    bind(ListAdUsersApi.class).toInstance(listAdUsersApi);
    bind(AdSyncDao.class).toInstance(new AdSyncDao() {
    });
  }
}
