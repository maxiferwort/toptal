package com.maxi.nutrition.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.model.VerificationToken;
import com.maxi.nutrition.repository.VerificationTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class ControllerTest {

  public static final String AUTHORIZATION = "Authorization";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  public Long createUser(User user) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    return Long.valueOf(response);
  }

  @Test
  public void main() {

  }

  public String login(String username, String password) throws Exception {
    String token;
    token = mockMvc.perform(
        post("/login").param(USERNAME, username).param(PASSWORD, password))
        .andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION);
    return token;
  }

  public void confirmToken(Long id) throws Exception {
    VerificationToken verificationToken = verificationTokenRepository.findByUserId(id).stream()
        .findAny().get();
    mockMvc.perform(get("/confirm?token=" + verificationToken.getToken()))
        .andExpect(status().isOk());
  }

  public void deleteByAdmin(Long id) throws Exception {
    String adminToken = mockMvc.perform(
        post("/login").param(USERNAME, "admin").param(PASSWORD, "admin"))
        .andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION);
    mockMvc.perform(delete("/users/{userId}", id).header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk());
  }

  public User getUser(Long id, String token) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String userJson = mockMvc.perform(get("/users/{userId}", id).header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(userJson, User.class);
  }

}
