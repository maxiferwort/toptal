package com.maxi.nutrition.security;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private static final Integer MAX_ATTEMPTS = 3;
  private AuthenticationManager authenticationManager;

  private JwtService jwtService;

  private UserRepository userRepository;

  private String jsonUsername;

  private String jsonPassword;

  public LoginFilter(
      AuthenticationManager authenticationManager, JwtService jwtService,
      UserRepository userRepository) {
    super();
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest req,
      HttpServletResponse res, FilterChain chain,
      Authentication auth) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    User user = userRepository.findByUsername(auth.getName().toLowerCase());
    user.setAttempts(0);
    userRepository.save(user);
    jwtService.addAuthentication(response, auth.getName());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    String username = obtainUsername(request);
    User user = userRepository.findByUsername(username.toLowerCase());
    user.setAttempts(user.getAttempts() + 1);
    userRepository.save(user);
    super.unsuccessfulAuthentication(request, response, failed);
  }

  @Override
  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }
}
