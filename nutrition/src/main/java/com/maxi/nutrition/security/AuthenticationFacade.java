package com.maxi.nutrition.security;

import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.RoleRepository;
import com.maxi.nutrition.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authenticationfacade")
public class AuthenticationFacade {

  private static final Logger logger = LoggerFactory
      .getLogger(AuthenticationFacade.class);

  @Autowired
  private RoleHierarchy roleHierarchy;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  public boolean DeleteEnabled(Long id) {
    return isAdministrator(id) && !isOwner(id.toString());
  }

  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public boolean isOwner(String userId) {
    if (StringUtils.isNumeric(userId)) {
      Authentication authentication = getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        if (authentication.getPrincipal() instanceof PrincipalUserDetail) {
          PrincipalUserDetail principalUserDetail = (PrincipalUserDetail) getAuthentication()
              .getPrincipal();
          return principalUserDetail.getId().equals(Long.valueOf(userId));
        }
      }
    }
    return false;
  }

  public boolean isAdministrator(Long id) {
    String[] roles = roleRepository.findByUserId(id).stream()
        .map(Role::getRole)
        .toArray(String[]::new);
    Authentication authentication = getAuthentication();
    List<String> authorities = getReacheableAuthorities();
    List<String> userAuthorities = AuthorityUtils.createAuthorityList(roles).stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    return authorities.containsAll(userAuthorities);
  }

  public boolean isOwner(User user) {
    Authentication authentication = getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      if (authentication.getPrincipal() instanceof PrincipalUserDetail) {
        PrincipalUserDetail principalUserDetail = (PrincipalUserDetail) getAuthentication()
            .getPrincipal();
        return principalUserDetail.getId().equals(user.getId());
      }
    }
    return authentication.getName().equals(user.getUsername());
  }

  public boolean canAddRole(String role) {
    if (StringUtils.isEmpty(role)) {
      return false;
    }
    return getReacheableAuthorities().contains("ROLE_" + role.toUpperCase());
  }

  private List<String> getReacheableAuthorities() {
      return roleHierarchy
        .getReachableGrantedAuthorities(getAuthentication().getAuthorities()).stream()
        .map(Objects::toString).collect(Collectors.toList());
  }
}
