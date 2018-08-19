package com.maxi.nutrition.validator.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return MultipartFile.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    MultipartFile file = (MultipartFile) target;
    if (file.isEmpty()) {
      errors.rejectValue("file", "file.empty");
    }
  }
}
