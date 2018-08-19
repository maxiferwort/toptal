package com.maxi.nutrition.controller;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.RoleRepository;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends ControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserService userService;

  @Test
  public void testUserNotNull() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String response = mockMvc
        .perform(post("/signup").content("").contentType(APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn()
        .getResponse()
        .getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
    assertThat(errorResponse.getMessage(), is("Required request body is missing or unreadable"));
    assertThat(errorResponse.getCode(), is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  public void testUsernameNotBlank() throws Exception {
    User user = createRandomUser();
    user.setUsername("");
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn()
        .getResolvedException()
        .getMessage();
    assertTrue(response.contains("Username must not be blank"));
    assertTrue(response.contains("NotBlank.user.username"));
  }

  @Test
  public void testEmailNotBlank() throws Exception {
    User user = createRandomUser();
    user.setEmail("");
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn()
        .getResolvedException()
        .getMessage();
    assertTrue(response.contains("Email must not be blank"));
    assertTrue(response.contains("NotBlank.user.email"));
  }

  @Test
  public void testPasswordNotBlank() throws Exception {
    User user = createRandomUser();
    user.setPassword("");
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn()
        .getResolvedException()
        .getMessage();
    assertTrue(response.contains("Password must not be blank"));
    assertTrue(response.contains("NotBlank.user.password"));
  }

  @Test
  public void testPasswordWrongPattern() throws Exception {
    User user = createRandomUser();
    user.setPassword("!@#!%!@#");
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn()
        .getResolvedException()
        .getMessage();
    assertTrue(
        response.contains("Password can only consist of numbers, letters, underscore or hyphen."));
    assertTrue(response.contains("Pattern.user.password"));
  }

  @Test
  public void testConfirmNullToken() throws Exception {
    mockMvc.perform(get("/confirm?token=asdas"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testConfirmToken() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    User bd = userRepository.findById(id).get();
    assertFalse(bd.getEmailConfirmation());
    confirmToken(id);
    bd = userRepository.findById(id).get();
    assertTrue(bd.getEnabled());

    String token = mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername())
            .param(PASSWORD, user.getPassword()))
        .andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION);
    assertNotNull(token);
    userRepository.deleteById(id);
  }

  @Test
  public void testDelete() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, user.getPassword()))
        .andExpect(status().isOk()).andReturn().getResponse().getHeader(AUTHORIZATION);
    mockMvc.perform(delete("/users/{userId}", id).header(AUTHORIZATION, token))
        .andExpect(status().isForbidden());
    deleteByAdmin(id);
  }



  @Test
  public void testUpdate() throws Exception {
    User user = createRandomUser();
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    Long id = Long.valueOf(response);
    mockMvc.perform(get("/users/{userId}", id))
        .andExpect(status().isUnauthorized());
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());

    User fromJson = getUser(id, token);
    String userJson;
    assertThat(user.getEmail().toLowerCase(), is(fromJson.getEmail()));
    assertThat(user.getUsername().toLowerCase(), is(fromJson.getUsername()));

    user.setUsername(randomAlphanumeric(10));

    mockMvc.perform(put("/users/{userId}", id).content(objectMapper.writeValueAsString(user))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

    token = login(fromJson.getUsername(), user.getPassword());

    user.setId(id);
    mockMvc.perform(put("/users/{userId}", id).content(objectMapper.writeValueAsString(user))
        .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isOk());


    mockMvc.perform(get("/users/{userId}", id).header(AUTHORIZATION, token))
        .andExpect(status().isUnauthorized());

    token = login(user.getUsername(), user.getPassword());

    userJson = mockMvc.perform(get("/users/{userId}", id).header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    fromJson = objectMapper.readValue(userJson, User.class);
    assertThat(user.getUsername().toLowerCase(), is(fromJson.getUsername()));
    deleteByAdmin(id);
  }

  @Test
  public void testUpdateWithEmailUseByAnotherUser() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    User user = createRandomUser();
    Long id = createUser(user);
    User user2 = createRandomUser();
    Long id2 = createUser(user2);
    confirmToken(id2);
    String token = login(user2.getUsername(), user2.getPassword());
    user2.setId(id2);
    String email = user2.getEmail();
    user2.setEmail(user.getEmail());
    String response = mockMvc
        .perform(put("/users/{userId}", id2).content(objectMapper.writeValueAsString(user2))
            .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isBadRequest()).andReturn().getResolvedException().getMessage();
    assertTrue(response.contains("Email already in use"));
    user2.setEmail(email).setPassword(user2.getPassword() + "2");
    mockMvc
        .perform(put("/users/{userId}", id2).content(objectMapper.writeValueAsString(user2))
            .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isOk());
    login(user2.getUsername(), user2.getPassword());
    deleteByAdmin(id);
    deleteByAdmin(id2);
  }

  @Test
  public void testUpdateWithUsernameUseByAnotherUser() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    User user = createRandomUser();
    Long id = createUser(user);
    User user2 = createRandomUser();
    Long id2 = createUser(user2);
    confirmToken(id2);
    String token = login(user2.getUsername(), user2.getPassword());
    user2.setId(id2);
    String username = user2.getUsername();
    user2.setUsername(user.getUsername());
    String response = mockMvc
        .perform(put("/users/{userId}", id2).content(objectMapper.writeValueAsString(user2))
            .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isBadRequest()).andReturn().getResolvedException().getMessage();
    assertTrue(response.contains("Username already in use"));
    user2.setUsername(username).setPassword(user2.getPassword() + "2");
    mockMvc
        .perform(put("/users/{userId}", id2).content(objectMapper.writeValueAsString(user2))
            .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isOk());
    login(user2.getUsername(), user2.getPassword());
    deleteByAdmin(id);
    deleteByAdmin(id2);
  }


  @Test
  public void testSignUp_withWrongContent() throws Exception {
    User user = createRandomUser();
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    mockMvc.perform(post("/signup").content(objectMapper.writeValueAsString(user))
        .contentType(APPLICATION_XML_VALUE))
        .andExpect(status().isUnsupportedMediaType());
  }

  @Test
  public void testCreateUser() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);

    assertNotNull(id);

    confirmToken(id);

    mockMvc.perform(get("/users/{userId}", id)).andExpect(status().isUnauthorized());

    String token = login(user.getUsername(), user.getPassword());

    User fromJson = getUser(id, token);

    assertThat(user.getEmail().toLowerCase(), is(fromJson.getEmail()));
    assertThat(user.getUsername().toLowerCase(), is(fromJson.getUsername()));
    deleteByAdmin(id);
  }

  @Test
  public void testCreateUser_withEmailAlreadyInUse() throws Exception {
    User user = createRandomUser();
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    Long id = Long.valueOf(response);
    user.setUsername(user.getUsername() + "2");
    mockMvc.perform(post("/signup").content(objectMapper.writeValueAsString(user))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    deleteByAdmin(id);
  }

  @Test
  public void testCreateUser_withUsernameAlreadyInUse() throws Exception {
    User user = createRandomUser();
    ObjectMapper objectMapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false);
    String response = mockMvc
        .perform(post("/signup").content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsString();
    Long id = Long.valueOf(response);
    user.setEmail("2" + user.getEmail());
    mockMvc.perform(post("/signup").content(objectMapper.writeValueAsString(user))
        .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    deleteByAdmin(id);
  }

  @Test
  public void testThreeLoginAttemptsBlockAccount() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    assertNotNull(getUser(id, token));
    mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, "wrong password"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, "wrong password"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, "wrong password"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(get("/users/{userId}", id).header(AUTHORIZATION, token))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testLoginResetsAttempts() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    assertNotNull(login(user.getUsername(), user.getPassword()));
    mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, "wrong password"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(
        post("/login").param(USERNAME, user.getUsername()).param(PASSWORD, "wrong password"))
        .andExpect(status().isUnauthorized());
    assertNotNull(login(user.getUsername(), user.getPassword()));
  }


}

