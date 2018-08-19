package com.maxi.nutrition.controller;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maxi.nutrition.model.NutritionEntry;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.rest.CustomPageImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NutritionControllerTest extends ControllerTest {

  public static final String HAMBURGUER = "hamburguer";

  @Test
  public void testCreateNutritionEntry() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    NutritionEntry nutritionEntry = createNutritionEntry(HAMBURGUER);
    Long idEntry = postNutritionEntry(id, token, nutritionEntry);
    deleteByAdmin(id);
  }

  @Test
  public void testPaginatedNutritionEntries() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    List<Long> ids = Lists.newArrayList();
    for (int i = 0; i < 20; i++) {
      NutritionEntry nutritionEntry = createNutritionEntry(HAMBURGUER);
      Long idEntry = postNutritionEntry(id, token, nutritionEntry);
      ids.add(idEntry);
    }
    String response = mockMvc
        .perform(get("/users/{userId}/nutrition", id).header(AUTHORIZATION, token))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    JavaType collectionType = objectMapper.getTypeFactory().constructParametricType
        (CustomPageImpl.class, NutritionEntry.class);

    CustomPageImpl<NutritionEntry> customPage = objectMapper.readValue(response, collectionType);

    Assert.assertTrue(
        customPage.getContent().stream().mapToLong(NutritionEntry::getId).boxed().collect(
            Collectors.toList()).containsAll(ids));
    deleteByAdmin(id);
  }


  @Test
  public void testTotalNutritionCalories() throws Exception {
    User user = createRandomUser();
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    NutritionEntry nutritionEntry = createNutritionEntry(HAMBURGUER).setCalories(1000);
    Long idEntry = postNutritionEntry(id, token, nutritionEntry);
    NutritionEntry entry = getNutritionEntry(id, token, idEntry);
    Assert.assertTrue(entry.getTotalCalories());
    NutritionEntry nutritionEntry2 = createNutritionEntry(HAMBURGUER).setCalories(500);
    Long idEntry2 = postNutritionEntry(id, token, nutritionEntry2);
    NutritionEntry entry2 = getNutritionEntry(id, token, idEntry2);
    Assert.assertTrue(entry2.getTotalCalories());
    NutritionEntry nutritionEntry3 = createNutritionEntry(HAMBURGUER).setCalories(500);
    Long idEntry3 = postNutritionEntry(id, token, nutritionEntry3);
    NutritionEntry entry3 = getNutritionEntry(id, token, idEntry3);
    Assert.assertFalse(entry3.getTotalCalories());
    deleteByAdmin(id);
  }

  @Test
  public void testWithCustomTotal() throws Exception {
    User user = createRandomUser().setExpectedNumberCalories(500);
    Long id = createUser(user);
    confirmToken(id);
    String token = login(user.getUsername(), user.getPassword());
    NutritionEntry nutritionEntry = createNutritionEntry(HAMBURGUER).setCalories(1000);
    Long idEntry = postNutritionEntry(id, token, nutritionEntry);
    NutritionEntry entry = getNutritionEntry(id, token, idEntry);
    Assert.assertFalse(entry.getTotalCalories());
    deleteByAdmin(id);
  }

  private NutritionEntry getNutritionEntry(Long id, String token, Long idEntry) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String response = mockMvc.perform(
        get("/users/{userId}/nutrition/{nutritionId}", id, idEntry).header(AUTHORIZATION, token))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(response, NutritionEntry.class);
  }

  private Long postNutritionEntry(Long id, String token, NutritionEntry nutritionEntry)
      throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String response = mockMvc
        .perform(
            post("/users/{userId}/nutrition", id)
                .content(objectMapper.writeValueAsString(nutritionEntry))
                .contentType(APPLICATION_JSON).header(AUTHORIZATION, token))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return Long.valueOf(response);
  }

  private NutritionEntry createNutritionEntry(String meal) {
    return new NutritionEntry()
        .setDate(LocalDate.now())
        .setMealName(meal)
        .setTime(LocalDateTime.now().minusMinutes(1));
  }

  @Test
  public void testPageableNutrtionEntry() {

  }
}
