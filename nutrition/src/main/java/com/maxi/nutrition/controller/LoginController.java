package com.maxi.nutrition.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  @PostMapping("/login")
  public void login(@RequestParam String username, @RequestParam String password) {

  }

}
