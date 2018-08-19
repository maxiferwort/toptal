package com.maxi.nutrition.model;

import java.util.List;
import lombok.Data;

@Data
public class NutritionixResponse {

  private List<Food> foods;

  private String message;
}
