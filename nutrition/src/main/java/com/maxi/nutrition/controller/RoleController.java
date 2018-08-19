package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.service.RoleService;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class RoleController {

  @Autowired
  private RoleService roleService;

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) and !@authenticationfacade.isOwner(#userId) and @authenticationfacade.canAddRole(#role)")
  @PostMapping("/users/{userId}/roles")
  public void addRole(@PathVariable Long userId, @RequestParam @NotEmpty String role,
      @RequestHeader String Authorization) {
    roleService.createRole(userId, role);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}/roles")
  public List<Role> getRoles(@PathVariable Long userId, @RequestHeader String Authorization) {
    return roleService.findByUserId(userId);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) and !@authenticationfacade.isOwner(#userId)")
  @DeleteMapping("/users/{userID}/roles")
  public void deleteRole(@PathVariable Long userId, @RequestParam @NotEmpty String role,
      @RequestHeader String Authorization) {
    roleService.deleteRole(userId, role);
  }

}
