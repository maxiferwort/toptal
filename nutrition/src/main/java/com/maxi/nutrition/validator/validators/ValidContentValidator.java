package com.maxi.nutrition.validator.validators;

import com.maxi.nutrition.validator.interfaces.ValidContent;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidContentValidator implements
    ConstraintValidator<ValidContent, MultipartFile> {

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null || file.isEmpty()) {
      return false;
    } else {
      return file.getContentType().equals("image/jpeg");
    }
  }
}
