package com.maxi.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maxi.nutrition.validator.groups.OnCreate;
import com.maxi.nutrition.validator.groups.OnRegister;
import com.maxi.nutrition.validator.groups.OnUpdate;
import com.maxi.nutrition.validator.interfaces.NotOtherUserWithEmail;
import com.maxi.nutrition.validator.interfaces.NotOtherUserWithUsername;
import com.maxi.nutrition.validator.interfaces.NotUsedEmail;
import com.maxi.nutrition.validator.interfaces.NotUsedUsername;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "users")
@Data
@NotOtherUserWithEmail(groups = {OnUpdate.class})
@NotOtherUserWithUsername(groups = {OnUpdate.class})
public class User {

  private static final String TRANSIENT_PASSWORD = "TransientPassword";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  @NotNull(groups = {OnUpdate.class})
  private Long id;

  @NotBlank(groups = {OnUpdate.class, OnCreate.class}, message = "Username must not be blank.")
  @Size(groups = {OnUpdate.class,
      OnCreate.class}, min = 5, max = 40, message = "Username should have min 5 and max 40 characters.")
  @Pattern(groups = {OnUpdate.class,
      OnCreate.class}, regexp = "^[A-Za-z0-9_-]{5,40}", message = "Username can only consist of numbers, letters, underscore or hyphen.")
  @NotUsedUsername(groups = {OnCreate.class})
  private String username;

  @NotBlank(groups = {OnUpdate.class, OnCreate.class}, message = "Email must not be blank")
  @Email(groups = {OnUpdate.class, OnCreate.class}, message = "Email has bad format")
  @NotUsedEmail(groups = {OnCreate.class})
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotBlank(groups = {OnUpdate.class, OnCreate.class}, message = "Password must not be blank.")
  @Size(groups = {OnUpdate.class,
      OnCreate.class}, min = 5, max = 40, message = "Password should have min 5 and max 40 characters.")
  @Pattern(groups = {OnUpdate.class,
      OnCreate.class}, regexp = "^[A-Za-z0-9_-]{5,40}", message = "Password can only consist of numbers, letters, underscore or hyphen.")
  @Transient
  private String password;

  @JsonIgnore
  private String encodedPassword;

  @JsonIgnore
  private Boolean enabled;

  @JsonIgnore
  private String picture;

  @JsonIgnore
  private Integer attempts;

  @JsonIgnore
  private Boolean emailConfirmation;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  @JsonIgnore
  private List<VerificationToken> verificationTokens;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  @JsonIgnore
  private List<Role> roles;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
  @JsonIgnore
  private List<NutritionEntry> nutritionEntries;

  @NotBlank(groups = {OnRegister.class}, message = "First name must not be blank.")
  @Size(groups = {
      OnRegister.class}, min = 1, max = 40, message = "First name should have min 1 and max 40 characters.")
  @Pattern(groups = {
      OnRegister.class}, regexp = "^[a-zA-Z]{5,40}", message = "First name can only consist of letters")
  private String firstName;

  @NotBlank(groups = {OnRegister.class}, message = "Last name must not be blank.")
  @Size(groups = {
      OnRegister.class}, min = 1, max = 40, message = "Last name should have min 1 and max 40 characters.")
  @Pattern(groups = {
      OnRegister.class}, regexp = "^[a-zA-Z]{5,40}", message = "Last name can only consist of letters")
  private String lastName;

  @Positive(groups = {OnUpdate.class, OnCreate.class, OnRegister.class})
  @NotNull(groups = {OnUpdate.class})
  private Integer expectedNumberCalories;

  @PrePersist
  public void prePersist() {
    password = TRANSIENT_PASSWORD;
  }

  @PreUpdate
  public void preUpdate() {
    password = TRANSIENT_PASSWORD;
  }

}
