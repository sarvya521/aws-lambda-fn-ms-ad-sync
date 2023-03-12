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
@Table(name = "ad_synaudit")
public class AdSyncAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "total_users_added", nullable = false)
  private int totalUsersAdded;

  @Column(name = "total_users_removed", nullable = false)
  private int totalUsersRemoved;

  @Column(name = "is_success", nullable = false)
  private boolean isSuccess;

  @Column(name = "errors")
  private String errors;

  @Column(name = "date", nullable = false)
  private Instant ts;

  public AdSyncAudit() {
  }

  public AdSyncAudit(UUID orgId, int totalUsersAdded, int totalUsersRemoved, boolean isSuccess,
      String errors, Instant ts) {
    this.orgId = orgId;
    this.totalUsersAdded = totalUsersAdded;
    this.totalUsersRemoved = totalUsersRemoved;
    this.isSuccess = isSuccess;
    this.errors = errors;
    this.ts = ts;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getOrgId() {
    return orgId;
  }

  public void setOrgId(UUID orgId) {
    this.orgId = orgId;
  }

  public int getTotalUsersAdded() {
    return totalUsersAdded;
  }

  public void setTotalUsersAdded(int totalUsersAdded) {
    this.totalUsersAdded = totalUsersAdded;
  }

  public int getTotalUsersRemoved() {
    return totalUsersRemoved;
  }

  public void setTotalUsersRemoved(int totalUsersRemoved) {
    this.totalUsersRemoved = totalUsersRemoved;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean success) {
    isSuccess = success;
  }

  public String getErrors() {
    return errors;
  }

  public void setErrors(String errors) {
    this.errors = errors;
  }

  public Instant getTs() {
    return ts;
  }

  public void setTs(Instant ts) {
    this.ts = ts;
  }

  @Override
  public String toString() {
    return "AdSyncAudit{" +
        "id=" + id +
        ", orgId=" + orgId +
        ", totalUsersAdded=" + totalUsersAdded +
        ", totalUsersRemoved=" + totalUsersRemoved +
        ", isSuccess=" + isSuccess +
        ", errors='" + errors + '\'' +
        ", ts=" + ts +
        '}';
  }
}