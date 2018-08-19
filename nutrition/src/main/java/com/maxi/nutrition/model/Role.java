package com.maxi.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.Data;

@Entity
@Table(name = "user_roles")
@Data
public class Role {

  @NotEmpty
  private String role;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_role_id")
  @JsonIgnore
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

}
