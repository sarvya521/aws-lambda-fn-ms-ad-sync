package com.sp.fn.adsync.config;

public class Config {

  private String listUsersEndpoint;

  private String adTokenEndpoint;

  private Datasource datasource;

  public String getListUsersEndpoint() {
    return listUsersEndpoint;
  }

  public void setListUsersEndpoint(String listUsersEndpoint) {
    this.listUsersEndpoint = listUsersEndpoint;
  }

  public String getAdTokenEndpoint() {
    return adTokenEndpoint;
  }

  public void setAdTokenEndpoint(String adTokenEndpoint) {
    this.adTokenEndpoint = adTokenEndpoint;
  }

  public Datasource getDatasource() {
    return datasource;
  }

  public void setDatasource(Datasource datasource) {
    this.datasource = datasource;
  }
}
