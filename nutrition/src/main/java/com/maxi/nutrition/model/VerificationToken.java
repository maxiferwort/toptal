package com.maxi.nutrition.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "verification_tokens")
@Data
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "token_id")
  private Long id;

  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  private String token;

  private LocalDateTime expiration;

}


