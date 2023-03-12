package com.sp.fn.adsync.entity;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "app_user_roles_audit")
public class UserRoleAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "userrole_id", nullable = false)
  private UUID userRoleId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "date", insertable = false, updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
      nullable = false)
  private Instant ts;

  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "agent", nullable = false)
  private String agent = "00000000-0000-0000-0000-000000000000";

  public UserRoleAudit() {
  }

  public UserRoleAudit(UserRole userRole) {
    this.userRoleId = userRole.getId();
    this.userId = userRole.getUserId();
    this.roleId = userRole.getRoleId();
    this.isActive = userRole.isActive();
    this.version = userRole.getVersion();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getUserRoleId() {
    return userRoleId;
  }

  public void setUserRoleId(UUID userRoleId) {
    this.userRoleId = userRoleId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getRoleId() {
    return roleId;
  }

  public void setRoleId(UUID roleId) {
    this.roleId = roleId;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
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
