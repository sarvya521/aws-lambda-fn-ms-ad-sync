package com.sp.fn.adsync.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "app_role")
public class Role {

  /**
   * id of the role.
   */
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", columnDefinition = "BINARY(16) DEFAULT (UUID_TO_BIN(UUID(), TRUE))")
  private UUID id;

  /**
   * role name.
   */
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  /**
   * tells us whether this role is active or not.
   */
  @Column(name = "is_active")
  private boolean isActive;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}
