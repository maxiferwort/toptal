package com.maxi.nutrition.security;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import com.maxi.nutrition.controller.UserController;
import com.maxi.nutrition.repository.RoleRepository;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.service.UserService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityTests {

  public static final String ROLE_USER_MANAGER = "ROLE_USER_MANAGER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_USER = "ROLE_USER";
  @Autowired
  private UserController userController;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private RoleHierarchy roleHierarchy;

  @Test
  public void testAuthoritiesAdmin() {
    Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) roleHierarchy
        .getReachableGrantedAuthorities(createAuthorityList(ROLE_ADMIN));
    assertTrue(grantedAuthorities.size() == 3);
    List<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertTrue(authorities.contains(ROLE_USER_MANAGER));
    assertTrue(authorities.contains(ROLE_USER));
    assertTrue(authorities.contains(ROLE_ADMIN));
  }

  @Test
  public void testAuthoritiesUserManager() {
    Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) roleHierarchy
        .getReachableGrantedAuthorities(createAuthorityList(ROLE_USER_MANAGER));
    List<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertTrue(grantedAuthorities.size() == 2);
    assertTrue(authorities.contains(ROLE_USER_MANAGER));
    assertTrue(authorities.contains(ROLE_USER));
  }

  @Test
  public void testAuthoritiesUse() {
    Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) roleHierarchy
        .getReachableGrantedAuthorities(createAuthorityList(ROLE_USER));
    List<String> authorities = grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertTrue(grantedAuthorities.size() == 1);
    assertTrue(authorities.contains(ROLE_USER));
  }

}
