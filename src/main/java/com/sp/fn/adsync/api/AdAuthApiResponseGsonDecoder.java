package com.sp.fn.adsync.api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import feign.gson.GsonDecoder;

public class AdAuthApiResponseGsonDecoder extends GsonDecoder {

  public AdAuthApiResponseGsonDecoder() {
    super(new GsonBuilder()
        .registerTypeAdapter(AdAuthApiResponse.class,
            (JsonDeserializer<AdAuthApiResponse>) (json, type, jsonDeserializationContext) -> {
              JsonObject jsonObject = json.getAsJsonObject();
              String accessToken = jsonObject.get("access_token").getAsString();
              return new AdAuthApiResponse(accessToken);
            }).create());
  }
}
