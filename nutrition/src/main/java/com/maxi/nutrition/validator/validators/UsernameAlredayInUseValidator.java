package com.maxi.nutrition.validator.validators;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.validator.interfaces.NotUsedUsername;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameAlredayInUseValidator implements ConstraintValidator<NotUsedUsername, String> {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean isValid(String username, ConstraintValidatorContext context) {
    if (username == null) {
      return false;
    }
    User example = userRepository.findByUsername(username.toLowerCase());
    return example == null;

  }
}
