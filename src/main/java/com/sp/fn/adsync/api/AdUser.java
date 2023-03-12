package com.sp.fn.adsync.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AdUser {

  private UUID id;
  private String givenName;
  private String surname;
  private String mail;
  private boolean accountEnabled;
  private Instant deletedDateTime;

  public AdUser(UUID id, String givenName, String surname, String mail, boolean accountEnabled,
      Instant deletedDateTime) {
    this.id = id;
    this.givenName = givenName;
    this.surname = surname;
    this.mail = mail;
    this.accountEnabled = accountEnabled;
    this.deletedDateTime = deletedDateTime;
  }

  public UUID getId() {
    return id;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getSurname() {
    return surname;
  }

  public String getMail() {
    return mail;
  }

  public boolean isAccountEnabled() {
    return accountEnabled;
  }

  public Instant getDeletedDateTime() {
    return deletedDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdUser user = (AdUser) o;
    return mail.equals(user.mail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mail);
  }

  @Override
  public String toString() {
    return "AdUser{" +
        "id=" + id +
        ", givenName='" + givenName + '\'' +
        ", surname='" + surname + '\'' +
        ", mail='" + mail + '\'' +
        ", accountEnabled=" + accountEnabled +
        ", deletedDateTime=" + deletedDateTime +
        '}';
  }
}
