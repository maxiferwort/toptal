package com.maxi.nutrition.service;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.model.VerificationToken;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.repository.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


  public static final int EXPECTED_NUMBER_CALORIES = 2000;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EmailService emailService;
  @Autowired
  private VerificationTokenRepository verificationTokenRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public Long createUser(User user) {
    User persist = new User().setEnabled(true)
        .setEmailConfirmation(false)
        .setAttempts(0)
        .setEncodedPassword(passwordEncoder.encode(user.getPassword()))
        .setUsername(user.getUsername().toLowerCase())
        .setEmail(user.getEmail().toLowerCase())
        .setExpectedNumberCalories(user.getExpectedNumberCalories());
    if (persist.getExpectedNumberCalories() == null) {
      persist.setExpectedNumberCalories(EXPECTED_NUMBER_CALORIES);
    }
    Long id = userRepository.save(persist).getId();
    String token = createToken(persist);
    emailService.sendConfirmationEmail(persist, token);
    return id;
  }

  private String createToken(User user) {
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken()
        .setToken(token)
        .setUser(user)
        .setExpiration(LocalDateTime.now().plusDays(3));
    verificationTokenRepository.save(verificationToken);
    return token;
  }


  @PreAuthorize("@authenticationfacade.isAdministrator(#userId)")
  public void updateUser(User user, Long userId) {
    User bd = findUserById(userId);
    bd.setUsername(user.getUsername().toLowerCase())
        .setEmail(user.getEmail().toLowerCase())
        .setEncodedPassword(passwordEncoder.encode(user.getPassword()))
        .setExpectedNumberCalories(user.getExpectedNumberCalories());
    userRepository.save(bd);
  }


  public ResponseEntity confirmEmail(String token) {
    //GENERAR TOKEN CON USERNAME SEED
    VerificationToken verificationToken = getVerificationToken(token);
    if (verificationToken == null || verificationToken.getExpiration()
        .isBefore(LocalDateTime.now())) {
      throw new ResourceNotFoundException("Token not found or expirated");
    }
    confirmEmail(verificationToken);
    return new ResponseEntity("email verified", HttpStatus.OK);
  }

  public void confirmEmail(VerificationToken verificationToken) {
    User user = verificationToken.getUser();
    user.setEmailConfirmation(true);
    userRepository.save(user);
    verificationTokenRepository.delete(verificationToken);
  }

  public VerificationToken getVerificationToken(String token) {
    return verificationTokenRepository.findByTokenAndExpirationAfter(token, LocalDateTime.now());
  }


  @PostAuthorize("@authenticationfacade.isOwner(returnObject) or @authenticationfacade.isAdministrator(#userId)")
  public User findUserById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() ->
        new ResourceNotFoundException("User not found for id: " + userId));
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) && !@authenticationfacade.isOwner(#userId)")
  public void deleteById(Long userId) {
    User user = findUserById(userId);
    userRepository.deleteById(user.getId());
  }

  public void invite(String email) {
    emailService.sendInvitationEmail(email);
  }
}
