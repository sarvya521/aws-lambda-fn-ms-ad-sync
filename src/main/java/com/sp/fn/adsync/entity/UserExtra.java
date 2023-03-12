package com.sp.fn.adsync.entity;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "app_user_extra")
public class UserExtra {

  /**
   * id of the user
   */
  @Id
  @Column(name = "user_id", nullable = false)
  private UUID id;

  /*
   * external user id
   */
  @Column(name = "external_user_id")
  private String externalUserId;

  /*
   * external account type
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "external_account_type")
  private ExternalAccountType externalAccountType;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  public UserExtra() {
  }

  public UserExtra(UUID id) {
    this.id = id;
  }

  public UserExtra(UUID id, String externalUserId, ExternalAccountType externalAccountType) {
    this.id = id;
    this.externalUserId = externalUserId;
    this.externalAccountType = externalAccountType;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getExternalUserId() {
    return externalUserId;
  }

  public void setExternalUserId(String externalUserId) {
    this.externalUserId = externalUserId;
  }

  public ExternalAccountType getExternalAccountType() {
    return externalAccountType;
  }

  public void setExternalAccountType(ExternalAccountType externalAccountType) {
    this.externalAccountType = externalAccountType;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserExtra userExtra = (UserExtra) o;
    return id.equals(userExtra.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
