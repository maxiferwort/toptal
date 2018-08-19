package com.maxi.nutrition.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomPageImpl<T> {

  private static final long serialVersionUID = 1L;
  private int number;
  private int size;
  private int totalPages;
  private int numberOfElements;
  private long totalElements;
  private boolean previousPage;
  private boolean firstPage;
  private boolean nextPage;
  private boolean lastPage;
  private List<T> content;

  public PageImpl<T> getPage() {
    return null;
  }

}
