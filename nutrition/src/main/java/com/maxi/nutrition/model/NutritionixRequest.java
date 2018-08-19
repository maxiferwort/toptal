package com.maxi.nutrition.model;

import lombok.Data;

@Data
public class NutritionixRequest {

  private String query;

  private Integer num_servings = 1;

  private String aggregate = "string";

  private boolean line_delimited = false;

  private boolean use_raw_foods = false;

  private boolean include_subrecipe = false;

  private String timezone = "US/Eastern";

  private int meal_type = 0;

  private boolean use_branded_foods = false;

  private String locale = "en_US";

}
