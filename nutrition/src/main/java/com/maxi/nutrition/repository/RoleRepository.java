package com.maxi.nutrition.repository;

import com.maxi.nutrition.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RoleRepository extends JpaRepository<Role, Long> {

  List<Role> findByUserId(Long id);

  Optional<Role> findByRoleAndUserId(String role, Long userId);
}
