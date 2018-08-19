package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.service.UserService;
import com.maxi.nutrition.validator.interfaces.NotUsedEmail;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class InviteController {

  @Autowired
  private UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/invite")
  public void invite(@Email @NotEmpty @NotUsedEmail @RequestParam("email") String email) {
    userService.invite(email);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity handleConstraintViolationException(HttpServletRequest request,
      ConstraintViolationException e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage(e.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

}
