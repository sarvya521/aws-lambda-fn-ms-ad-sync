package com.sp.fn.adsync.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.net.URI;

public interface AdAuthApi {

  @RequestLine("GET")
  @Headers("Content-Type: application/x-www-form-urlencoded")
  AdAuthApiResponse getAccessToken(
      URI endpoint,
      @Param("grant_type") String grantType,
      @Param("scope") String scope,
      @Param("client_id") String clientId,
      @Param("client_secret") String clientSecret
  );
}
