package com.sp.fn.adsync.api;

import java.util.Set;

public class ListAdUsersApiResponse {

  private int count;
  private String nextLink;
  private Set<AdUser> users;

  public ListAdUsersApiResponse(int count, String nextLink, Set<AdUser> users) {
    this.count = count;
    this.nextLink = nextLink;
    this.users = users;
  }

  public int getCount() {
    return count;
  }

  public String getNextLink() {
    return nextLink;
  }

  public Set<AdUser> getUsers() {
    return users;
  }

  @Override
  public String toString() {
    return "ListAdUsersApiResponse{" +
        "count=" + count +
        ", nextLink='" + nextLink + '\'' +
        ", users=" + users +
        '}';
  }
}
