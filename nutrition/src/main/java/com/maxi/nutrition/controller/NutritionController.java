package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.model.NutritionEntry;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.service.NutritionEntryService;
import com.maxi.nutrition.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NutritionController {

  @Autowired
  private NutritionEntryService nutritionEntryService;

  @Autowired
  private UserService userService;

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @PostMapping("/users/{userId}/nutrition")
  public Long addNutritionEntry(@Valid @RequestBody NutritionEntry nutritionEntry,
      @PathVariable Long userId, @RequestHeader String Authorization) {
    User user = userService.findUserById(userId);
    return nutritionEntryService.createNutritionEntry(nutritionEntry, userId);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}/nutrition")
  public Page<NutritionEntry> findEntriesById(@PathVariable Long userId, Pageable pageable,
      @RequestHeader String Authorization) {
    return nutritionEntryService.findByUserId(userId, pageable);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}/nutrition/{nutritionId}")
  public NutritionEntry findById(@PathVariable Long userId, @PathVariable Long nutritionId,
      @RequestHeader String Authorization) {
    return nutritionEntryService.findByIdAndUserId(nutritionId, userId);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @DeleteMapping("/users/{userId}/nutrition/{nutritionId}")
  public void deleteNutritionEntry(@PathVariable long userId, @PathVariable Long nutritionId,
      @RequestHeader String Authorization) {
    NutritionEntry nutritionEntry = nutritionEntryService.findByIdAndUserId(nutritionId, userId);
    nutritionEntryService.deleteEntry(nutritionEntry);
  }


  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity handleHttpMessageNotReadableException(HttpServletRequest request,
      HttpMessageNotReadableException e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage(
                "Required request body is missing or unreadable. (time field format=ISO.DATE_TIME, date=ISO.DATE)"),
        HttpStatus.BAD_REQUEST);
  }

}
