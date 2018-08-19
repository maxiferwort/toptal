package com.maxi.nutrition;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maxi.nutrition.model.NutritionEntry;
import com.maxi.nutrition.rest.CustomPageImpl;
import java.io.IOException;

public class Main {

  static String response = "{\"content\":[{\"id\":770,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:11.074\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":771,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:12.23\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":772,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:12.511\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":773,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:12.789\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":774,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:13.078\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":775,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:13.352\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":776,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:13.644\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":777,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:13.933\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":778,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:14.205\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":779,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:14.495\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":780,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:14.918\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":781,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:15.171\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":782,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:15.489\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":783,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:15.749\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":784,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:16.005\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":785,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:16.289\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":786,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:16.596\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":787,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:16.842\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":788,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:17.179\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true},{\"id\":789,\"date\":\"2018-08-15\",\"time\":\"2018-08-15T10:59:17.422\",\"mealName\":\"hamburguer\",\"calories\":540,\"totalCalories\":true}],\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageSize\":20,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":1,\"totalElements\":20,\"size\":20,\"number\":0,\"sort\":{\"sorted\":false,\"unsorted\":true},\"numberOfElements\":20,\"first\":true}";

  public static void main(String[] args) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    JavaType collectionType = objectMapper.getTypeFactory().constructParametricType
        (CustomPageImpl.class, NutritionEntry.class);

    CustomPageImpl<NutritionEntry> customPage = objectMapper.readValue(response, collectionType);

    customPage.getContent().stream()
        .forEach(nutritionEntry -> System.out.println(nutritionEntry.getId()));

  }

}
