package com.sp.fn.adsync.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;

public class UnixTimestampAdapter extends TypeAdapter<Instant> {

  @Override
  public void write(JsonWriter out, Instant value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    out.value(value.toEpochMilli());
  }

  @Override
  public Instant read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return Instant.ofEpochMilli(in.nextLong());
  }

}