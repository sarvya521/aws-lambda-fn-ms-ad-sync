package com.sp.fn.adsync.api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import feign.gson.GsonDecoder;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ListAdUsersApiResponseGsonDecoder extends GsonDecoder {

  public ListAdUsersApiResponseGsonDecoder() {
    super(new GsonBuilder()
        .registerTypeAdapter(ListAdUsersApiResponse.class,
            (JsonDeserializer<ListAdUsersApiResponse>) (json, type, jsonDeserializationContext) -> {
              JsonObject jsonObject = json.getAsJsonObject();
              int count = 0;
              if (jsonObject.has("@odata.count")) {
                count = jsonObject.get("@odata.count").getAsInt();
              }
              String nextLink = null;
              if (jsonObject.has("@odata.nextLink")) {
                nextLink = jsonObject.get("@odata.nextLink").getAsString();
              }
              JsonArray jsonArray = jsonObject.getAsJsonArray("value");
              Set<AdUser> users = new HashSet<>();
              jsonArray.forEach(jsonElement -> {
                JsonObject object = jsonElement.getAsJsonObject();
                UUID id = UUID.fromString(object.get("id").getAsString());
                String givenName = null;
                if (!object.get("givenName").isJsonNull()) {
                  givenName = object.get("givenName").getAsString();
                }
                String surname = null;
                if (!object.get("surname").isJsonNull()) {
                  surname = object.get("surname").getAsString();
                }
                boolean accountEnabled = object.get("accountEnabled").getAsBoolean();
                String mail = object.get("mail").getAsString();
                Instant deletedDateTime = null;
                if (!object.get("deletedDateTime").isJsonNull()) {
                  deletedDateTime = Instant.parse(object.get("deletedDateTime").getAsString());
                }
                users.add(
                    new AdUser(id, givenName, surname, mail, accountEnabled, deletedDateTime));
              });
              return new ListAdUsersApiResponse(count, nextLink, users);
            }).create());
  }
}
