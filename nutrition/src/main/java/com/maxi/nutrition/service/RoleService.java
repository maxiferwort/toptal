package com.maxi.nutrition.service;

import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  public static final String ROLE = "ROLE_";
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserService userService;

  public void createRole(Long userId, String roleName) {
    User user = userService.findUserById(userId);
    Optional<Role> optionalRole = roleRepository
        .findByRoleAndUserId(roleName.toUpperCase(), userId);
    if (!optionalRole.isPresent()) {
      Role role = new Role()
          .setRole(ROLE + roleName.toUpperCase())
          .setUser(user);
      roleRepository.save(role);
    }
  }

  public List<Role> findByUserId(Long userId) {
    User user = userService.findUserById(userId);
    return roleRepository.findByUserId(userId);
  }

  public void deleteRole(Long userId, String roleName) {
    Role role = roleRepository.findByRoleAndUserId(roleName, userId)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Role: " + roleName + " not found for id: " + userId));
    roleRepository.delete(role);
  }
}
