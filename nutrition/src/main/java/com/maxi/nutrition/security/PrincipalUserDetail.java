package com.maxi.nutrition.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalUserDetail extends User implements UserDetails {

  private Long id;

  public PrincipalUserDetail(String username, String password,
      Collection<? extends GrantedAuthority> authorities, Long id) {
    super(username, password, authorities);
    this.id = id;
  }

  public PrincipalUserDetail(String username, String password, boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities, Long id) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
    this.id = id;
  }

  public PrincipalUserDetail(String username, Long id) {
    super(username, "", Collections.emptyList());
    this.id = id;
  }

  public PrincipalUserDetail(String username, List<GrantedAuthority> authorityList, Long id) {
    super(username, "", authorityList);
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
