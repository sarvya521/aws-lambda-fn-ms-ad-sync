package com.sp.fn.adsync.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.net.URI;

public interface ListAdUsersApi {

  @RequestLine("GET")
  @Headers({"Authorization: {access_token}", "ConsistencyLevel: eventual"})
  ListAdUsersApiResponse get(URI endpoint, @Param("access_token") String accessToken);

}
