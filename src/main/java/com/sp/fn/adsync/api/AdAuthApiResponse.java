package com.sp.fn.adsync.api;

public class AdAuthApiResponse {

  private String token;

  public AdAuthApiResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
