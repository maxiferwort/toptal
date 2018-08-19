package com.maxi.nutrition.service;

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
public class NutritionixServiceFunctionalTest {

  @Autowired
  private NutritionixService nutritionixService;

  @Test
  public void testFindFood() {
    Assert.assertTrue(nutritionixService.findCalories("hamburguer") > 0);
  }

  @Test
  public void testFindFoodNotFound() {
    Assert.assertTrue(nutritionixService.findCalories("something that it isn't") == 0);
  }

}
