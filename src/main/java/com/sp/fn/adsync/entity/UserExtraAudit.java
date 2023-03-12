package com.sp.fn.adsync.entity;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "app_user_extra_audit")
public class UserExtraAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id")
  private UUID userId;

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

  @Column(name = "date", insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
      nullable = false)
  private Instant ts;

  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "agent", nullable = false)
  private String agent = "00000000-0000-0000-0000-000000000000";

  public UserExtraAudit() {
  }

  public UserExtraAudit(UserExtra userExtra) {
    this.userId = userExtra.getId();
    this.externalUserId = userExtra.getExternalUserId();
    this.externalAccountType = userExtra.getExternalAccountType();
    this.version = userExtra.getVersion();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
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

  public Instant getTs() {
    return ts;
  }

  public void setTs(Instant ts) {
    this.ts = ts;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }
}
