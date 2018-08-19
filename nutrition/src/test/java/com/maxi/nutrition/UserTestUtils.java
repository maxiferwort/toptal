package com.maxi.nutrition;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import com.maxi.nutrition.model.User;

public class UserTestUtils {

  public static User createRandomUser() {
    return new User().setUsername(randomAlphanumeric(20) + System.currentTimeMillis())
        .setEmail(randomAlphanumeric(25) + System.currentTimeMillis() + "@test.com")
        .setPassword(randomAlphanumeric(10))
        .setExpectedNumberCalories(2000);
  }

}
