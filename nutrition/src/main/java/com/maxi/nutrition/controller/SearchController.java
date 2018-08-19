package com.maxi.nutrition.controller;

import com.google.common.collect.Lists;
import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.model.NutritionEntry;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.repository.NutritionEntryRepository;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.rsql.CustomRsqlVisitor;
import com.maxi.nutrition.service.NutritionEntryService;
import cz.jirutka.rsql.parser.ParseException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class SearchController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NutritionEntryService nutritionEntryService;

  @Autowired
  private NutritionEntryRepository nutritionEntryRepository;

  public Specification<NutritionEntry> matchUserId(final List<Long> groupIds) {
    return new Specification<NutritionEntry>() {
      public Predicate toPredicate(Root<NutritionEntry> root, CriteriaQuery<?> query,
          CriteriaBuilder builder) {
        final Path<User> group = root.<User>get("user");
        return group.in(groupIds);
      }
    };
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/users/search")
  public Page<User> findUserByQuery(Pageable pageable,
      @RequestParam @NotEmpty String query, @RequestHeader String Authorization) {
    Node rootNode = new RSQLParser().parse(query);
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>());
    return userRepository.findAll(spec, pageable);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}/nutrition/search")
  public Page<NutritionEntry> findUserNutritionEntriesByQuery(Pageable pageable,
      @RequestParam @NotEmpty String query, @PathVariable Long userId,
      @RequestHeader String Authorization) {
    Node rootNode = new RSQLParser().parse(query);
    Specification<NutritionEntry> spec = rootNode.accept(new CustomRsqlVisitor<>());
    return nutritionEntryRepository
        .findAll(spec.and(matchUserId(Lists.newArrayList(userId))), pageable);
  }

  @ExceptionHandler({ParseException.class, RSQLParserException.class,
      IllegalArgumentException.class})
  public ResponseEntity handleParseException(HttpServletRequest request,
      Exception e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage("Query format is wrong."),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity handleConstraintViolationException(HttpServletRequest request,
      ConstraintViolationException e) {
    return new ResponseEntity(
        new ErrorResponse().setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage(e.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

}
