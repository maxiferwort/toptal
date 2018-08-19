package com.maxi.nutrition.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class EnableUserFilter extends OncePerRequestFilter {

  private static final Integer MAX_ATTEMPTS = 3;

  private UserRepository userRepository;

  private JwtService jwtService;

  public EnableUserFilter(UserRepository userRepository, JwtService jwtService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = ((HttpServletRequest) request).getHeader("Authorization");
    String username = null;
    if (!StringUtils.isEmpty(token)) {
      username = jwtService.getUsernameFromToken(token);
    } else {
      username = request.getParameter("username");
    }
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    if (StringUtils.isEmpty(username)) {
      sendErrorResponse(httpResponse, "Username or authorization token not found");
    } else if (!username.matches("^[A-Za-z0-9_-]{5,40}")) {
      sendErrorResponse(httpResponse, "Invalid Username");
    } else {
      User user = userRepository.findByUsername(username.toLowerCase());
      if (user == null) {
        sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED.getReasonPhrase());
      } else if (!user.getEmailConfirmation()) {
        sendErrorResponse(httpResponse, "The email for this account is not yet confirmed");
      } else if (user.getAttempts() >= MAX_ATTEMPTS) {
        sendErrorResponse(httpResponse,
            "This account is blocked, too many login attempts");
      } else {
        filterChain.doFilter(request, response);
      }
    }
  }

  private void sendErrorResponse(HttpServletResponse httpResponse,
      String message) throws IOException {
    SecurityContextHolder.clearContext();
    httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ErrorResponse errorResponse = new ErrorResponse()
        .setCode(HttpStatus.UNAUTHORIZED.value())
        .setMessage(message);
    httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
  }

}
