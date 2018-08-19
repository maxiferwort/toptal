package com.maxi.nutrition.repository;

import com.maxi.nutrition.model.NutritionEntry;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface NutritionEntryRepository extends JpaRepository<NutritionEntry, Long>,
    JpaSpecificationExecutor {

  Page<NutritionEntry> findByUserId(Long userId, Pageable pageable);

  @Query(value =
      "select sum(n.calories) from NutritionEntry n where n.user.id ="
          + " :userId and n.date = :date")
  Long findTotalCalories(@Param("date") LocalDate date, @Param("userId") Long userId);

  Optional<NutritionEntry> findByIdAndUserId(Long userId, Long id);

}
