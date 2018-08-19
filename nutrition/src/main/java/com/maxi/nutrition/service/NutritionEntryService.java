package com.maxi.nutrition.service;

import com.maxi.nutrition.model.NutritionEntry;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.NutritionEntryRepository;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class NutritionEntryService {

  @Autowired
  private NutritionEntryRepository nutritionEntryRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private NutritionixService nutritionixService;

  public Long createNutritionEntry(NutritionEntry nutritionEntry, Long userId) {
    User user = userService.findUserById(userId);
    nutritionEntry.setUser(user);
    int calories = getCalories(nutritionEntry);
    if (user.getExpectedNumberCalories() != null) {
      Long totalCalories = nutritionEntryRepository.findTotalCalories(LocalDate.now(), userId);
      if (totalCalories == null) {
        totalCalories = 0l;
      }
      nutritionEntry
          .setTotalCalories(totalCalories + calories < user.getExpectedNumberCalories());
    } else {
      nutritionEntry.setTotalCalories(true);
    }
    nutritionEntry.setCalories(calories);
    return nutritionEntryRepository.save(nutritionEntry).getId();
  }

  private int getCalories(NutritionEntry nutritionEntry) {
    int calories = 0;
    if (nutritionEntry.getCalories() == null) {
      calories = nutritionixService.findCalories(nutritionEntry.getMealName());
      if (calories == 0 && StringUtils.isNoneEmpty(nutritionEntry.getText())) {
        calories = nutritionixService.findCalories(nutritionEntry.getText());
      }
    } else {
      calories = nutritionEntry.getCalories();
    }
    if (calories == 0) {
      throw new ResourceNotFoundException("Couldn't find meal calories");
    }
    return calories;
  }

  public Page<NutritionEntry> findByUserId(Long userId,
      Pageable pageable) {
    User user = userService.findUserById(userId);
    return nutritionEntryRepository.findByUserId(userId, pageable);
  }

  public NutritionEntry findByIdAndUserId(Long nutritionId, Long userId) {
    return nutritionEntryRepository.findByIdAndUserId(nutritionId, userId).orElseThrow(() ->
        new ResourceNotFoundException("Nutrition entry not found for id: " + nutritionId));
  }
}
