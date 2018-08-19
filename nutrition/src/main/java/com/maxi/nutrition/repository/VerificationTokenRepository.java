package com.maxi.nutrition.repository;

import com.maxi.nutrition.model.VerificationToken;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  List<VerificationToken> findByUserId(Long userId);

  VerificationToken findByTokenAndExpirationAfter(String token, LocalDateTime time);
}
