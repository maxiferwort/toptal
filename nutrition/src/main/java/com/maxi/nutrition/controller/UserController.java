package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.service.UserService;
import com.maxi.nutrition.validator.groups.OnCreate;
import com.maxi.nutrition.validator.groups.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping(value = "/signup")
  public Long signUp(@Validated({OnCreate.class}) @RequestBody User user) {
    return userService.createUser(user);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @PutMapping("/users/{userId}")
  public void updateUser(@Validated({OnUpdate.class}) @RequestBody User user,
      @PathVariable Long userId, @RequestHeader String Authorization) {
    userService.updateUser(user, userId);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}")
  public User getUser(@PathVariable Long userId, @RequestHeader String Authorization) {
    return userService.findUserById(userId);
  }

  @GetMapping("/confirm")
  public void confirmMail(@RequestParam String token) {
    userService.confirmEmail(token);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) and !@authenticationfacade.isOwner(#userId)")
  @DeleteMapping("/users/{userId}")
  public void deleteUser(@PathVariable Long userId, @RequestHeader String Authorization) {
    User user = userService.findUserById(userId);
    userService.deleteById(userId);
  }

}
