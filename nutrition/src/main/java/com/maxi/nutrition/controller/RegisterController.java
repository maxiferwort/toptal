package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.model.VerificationToken;
import com.maxi.nutrition.service.UserService;
import com.maxi.nutrition.validator.groups.OnCreate;
import com.maxi.nutrition.validator.groups.OnRegister;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RegisterController {

  @Autowired
  private UserService userService;

  @GetMapping(value = "/register")
  public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user,
      @RequestParam(required = false) String email) {
    if (StringUtils.isNoneEmpty(email)) {
      user.setEmail(email);
    }
    modelAndView.addObject("user", user);
    modelAndView.setViewName("register");
    return modelAndView;
  }

  @PostMapping("/register")
  public ModelAndView processRegistrationForm(ModelAndView modelAndView,
      @Validated({OnCreate.class, OnRegister.class}) @ModelAttribute("user") User user,
      BindingResult bindingResult, HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      modelAndView.setViewName("register");
    } else {
      userService.createUser(user);
      modelAndView.addObject("confirmationMessage",
          "A confirmation e-mail has been sent to " + user.getEmail());
      modelAndView.setViewName("register");
    }
    return modelAndView;
  }

  @GetMapping("/register/confirm")
  public ModelAndView confirm(ModelAndView modelAndView, @RequestParam("token") String token) {
    VerificationToken verificationToken = userService.getVerificationToken(token);
    if (verificationToken != null) {
      userService.confirmEmail(verificationToken);
      modelAndView.addObject("successMessage", "Your email has been confirmed!");
    } else {
      modelAndView.addObject("errorMessage", "Oops!  This is an invalid confirmation link.");
    }
    modelAndView.setViewName("confirm");
    return modelAndView;
  }

}
