package com.maxi.nutrition.exception;

import com.maxi.nutrition.model.ErrorResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory
      .getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity handleHttpMessageNotReadableException(HttpServletRequest request,
      HttpMessageNotReadableException e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage("Required request body is missing or unreadable"),
        HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler({MissingPathVariableException.class})
  public ResponseEntity handleMissingPathVariable(HttpServletRequest request,
      MissingPathVariableException ex) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage("Request path variable is missing: " + ex.getVariableName()),
        HttpStatus.BAD_REQUEST);
  }

}
