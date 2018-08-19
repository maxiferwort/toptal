package com.maxi.nutrition.validator.validators;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.validator.interfaces.NotUsedEmail;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailAlredayInUseValidator implements ConstraintValidator<NotUsedEmail, String> {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null) {
      return false;
    }
    User user = userRepository.findByEmail(email.toLowerCase());
    return user == null;
  }
}
