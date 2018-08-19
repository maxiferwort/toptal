package com.maxi.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ErrorResponse implements Serializable {

  private String message;
  private Integer code;

}