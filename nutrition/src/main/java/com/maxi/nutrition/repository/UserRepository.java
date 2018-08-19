package com.maxi.nutrition.repository;

import com.maxi.nutrition.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor {

  User findByUsername(String username);

  User findByEmail(String email);

  User findByUsernameAndEnabled(String username, Boolean enabled);

  boolean existsByEmail(String email);

  User findByIdNotAndEmail(Long id, String email);

  User findByIdNotAndUsername(Long id, String username);
}
