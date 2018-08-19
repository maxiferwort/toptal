package com.maxi.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "nutrition_entries")
@Data
public class NutritionEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "nutrition_entry_id")
  private Long id;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE)
  @PastOrPresent
  private LocalDate date;

  @NotNull
  @DateTimeFormat(iso = ISO.DATE_TIME)
  @PastOrPresent
  private LocalDateTime time;

  private String text;

  @NotEmpty
  private String mealName;

  @Positive(message = "Calories should be positive.")
  private Integer calories;

  private Boolean totalCalories;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

}
