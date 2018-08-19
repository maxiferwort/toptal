package com.maxi.nutrition.security;

import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.RoleRepository;
import com.maxi.nutrition.repository.UserRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtFilter extends BasicAuthenticationFilter {

  private static final Logger logger = LoggerFactory
      .getLogger(JwtFilter.class);

  private RoleRepository userRoleRepository;

  private UserRepository userRepository;

  private JwtService jwtService;

  public JwtFilter(
      AuthenticationManager authenticationManager,
      AuthenticationEntryPoint authenticationEntryPoint,
      RoleRepository userRoleRepository,
      UserRepository userRepository, JwtService jwtService) {
    super(authenticationManager, authenticationEntryPoint);
    this.userRoleRepository = userRoleRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String token = ((HttpServletRequest) request).getHeader("Authorization");
    if (!StringUtils.isEmpty(token)) {
      String username = jwtService.getUsernameFromToken(token);
      User user = userRepository.findByUsername(username);
      String[] roles = userRoleRepository.findByUserId(user.getId()).stream()
          .map(Role::getRole)
          .toArray(String[]::new);
      PrincipalUserDetail principalUserDetail = new PrincipalUserDetail(username,
          AuthorityUtils.createAuthorityList(roles), user.getId());
      Authentication authentication = new UsernamePasswordAuthenticationToken(principalUserDetail,
          null,
          AuthorityUtils.createAuthorityList(roles));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(request, response);
  }
}
