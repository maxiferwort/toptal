package com.maxi.nutrition.validator.validators;


import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.validator.interfaces.NotOtherUserWithUsername;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotOtherUserWithUsernameValidator implements
    ConstraintValidator<NotOtherUserWithUsername, User> {

  @Autowired
  private UserRepository userRepository;


  @Override
  public boolean isValid(User user, ConstraintValidatorContext context) {
    if (user == null || user.getId() == null || user.getUsername() == null) {
      return false;
    }
    User db = userRepository.findByIdNotAndUsername(user.getId(), user.getUsername().toLowerCase());
    return db == null;
  }
}
