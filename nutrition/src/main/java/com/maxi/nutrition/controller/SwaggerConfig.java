package com.maxi.nutrition.controller;

import static com.google.common.base.Predicates.not;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.maxi.nutrition.controller"))
        .paths(not(PathSelectors.regex("/error.*")))
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Nutrition REST API",
        "Nutrition REST API.",
        "1.0.0",
        "Terms of service",
        new Contact("Maximiliano Fernandez Wortman", null, "maxifwortman@gmail.com"),
        null, null, Collections.emptyList());
  }
}
