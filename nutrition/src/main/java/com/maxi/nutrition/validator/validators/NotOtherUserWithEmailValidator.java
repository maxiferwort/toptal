package com.maxi.nutrition.validator.validators;


import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.validator.interfaces.NotOtherUserWithEmail;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotOtherUserWithEmailValidator implements
    ConstraintValidator<NotOtherUserWithEmail, User> {

  @Autowired
  private UserRepository userRepository;


  @Override
  public boolean isValid(User user, ConstraintValidatorContext context) {
    if (user == null || user.getId() == null || user.getEmail() == null) {
      return false;
    }
    User db = userRepository.findByIdNotAndEmail(user.getId(), user.getEmail().toLowerCase());
    return db == null;
  }
}
