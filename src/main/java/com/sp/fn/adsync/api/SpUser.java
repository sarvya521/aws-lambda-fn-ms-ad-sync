package com.sp.fn.adsync.api;

import com.sp.fn.adsync.entity.ExternalAccountType;
import com.sp.fn.adsync.entity.User;
import java.util.UUID;

public class SpUser {

  private User user;

  private String externalUserId;

  private UUID roleId;

  private ExternalAccountType externalAccountType;

  public SpUser(User user, String externalUserId, UUID roleId,
      ExternalAccountType externalAccountType) {
    this.user = user;
    this.externalUserId = externalUserId;
    this.roleId = roleId;
    this.externalAccountType = externalAccountType;
  }

  public User getUser() {
    return user;
  }

  public String getExternalUserId() {
    return externalUserId;
  }

  public UUID getRoleId() {
    return roleId;
  }

  public ExternalAccountType getExternalAccountType() {
    return externalAccountType;
  }
}
