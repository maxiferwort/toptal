package com.maxi.nutrition.controller;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RoleControllerTest extends ControllerTest {

  public static final String ADMIN = "ADMIN";
  public static final String ROLE = "role";
  public static final String MANAGER = "USER_MANAGER";

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testAdminCanAddAdmin() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    String token = login("admin", "admin");
    createRole(id, token, ADMIN);
    List<Role> roles = getRoles(id, token);
    Assert.assertTrue(roles.stream().anyMatch(role -> role.getRole().contains(ADMIN)));
    deleteByAdmin(id);
  }

  @Test
  public void testManagerCannotAddAdmin() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    String token = login("manager", "manager");
    mockMvc.perform(
        post("/users/{userId}/roles", id).header(AUTHORIZATION, token).param(ROLE, ADMIN))
        .andExpect(status().isForbidden());
    deleteByAdmin(id);
  }

  @Test
  public void testManagerCanAddManager() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    String token = login("manager", "manager");
    createRole(id, token, MANAGER);
    List<Role> roles = getRoles(id, token);
    Assert.assertTrue(roles.stream().anyMatch(role -> role.getRole().contains(MANAGER)));
    deleteByAdmin(id);
  }

  @Test
  public void testManagerCannotDeleteAdmin() throws Exception {
    User admin = userRepository.findByUsername(ADMIN.toLowerCase());
    String token = login("manager", "manager");
    mockMvc.perform(delete("/users/{userId}", admin.getId()).header(AUTHORIZATION, token))
        .andExpect(status().isForbidden());
  }

  @Test
  public void managerCannotAddRoleHimself() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    String token = login("manager", "manager");
    createRole(id, token, MANAGER);
    mockMvc.perform(
        post("/users/{userId}/roles", id).header(AUTHORIZATION, token).param(ROLE, ADMIN))
        .andExpect(status().isForbidden());
    deleteByAdmin(id);
  }

  @Test
  public void testUserCannotCreateManager() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    User user2 = createRandomUser();
    Long id2 = createUser(user2);
    mockMvc.perform(
        post("/users/{userId}/roles", id2).header(AUTHORIZATION, token).param(ROLE, MANAGER))
        .andExpect(status().isForbidden());
    deleteByAdmin(id);
    deleteByAdmin(id2);
  }

  private void createRole(Long id, String token, String role) throws Exception {
    mockMvc.perform(
        post("/users/{userId}/roles", id).header(AUTHORIZATION, token).param(ROLE, role))
        .andExpect(status().isOk());
  }

  private List<Role> getRoles(Long id, String token) throws Exception {
    String response = mockMvc.perform(get("/users/{userId}/roles", id).header(AUTHORIZATION, token))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JavaType roleListType = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, Role.class);
    return new ObjectMapper().readValue(response, roleListType);
  }

}
