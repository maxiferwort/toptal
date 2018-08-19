package com.maxi.nutrition.service;


import com.maxi.nutrition.model.NutritionixRequest;
import com.maxi.nutrition.model.NutritionixResponse;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NutritionixService {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${nutritionix.url}")
  private String url;

  @Value("${nutritionix.app.id}")
  private String appid;

  @Value("${nutritionix.app.key}")
  private String appkey;


  public int findCalories(String food) {
    NutritionixRequest nutritionixRequest = new NutritionixRequest().setQuery(food);
    HttpEntity<NutritionixRequest> en = createJsonHttpEntity(nutritionixRequest);
    try {
      ResponseEntity<NutritionixResponse> response = restTemplate
          .postForEntity(url, en, NutritionixResponse.class);
      return response.getBody().getFoods().stream()
          .mapToInt(foo -> Double.valueOf(foo.getNf_calories()).intValue()).sum();
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        return 0;
      }
      throw ex;
    }
  }

  private <T> HttpEntity<T> createJsonHttpEntity(T body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("x-app-id", appid);
    httpHeaders.set("x-app-key", appkey);
    httpHeaders.set("x-remote-user-id", "0");
    return new HttpEntity<T>(body, httpHeaders);
  }

}
