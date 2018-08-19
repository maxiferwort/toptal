package com.maxi.nutrition.controller;

import com.maxi.nutrition.model.ErrorResponse;
import com.maxi.nutrition.service.ImageService;
import com.maxi.nutrition.validator.interfaces.ValidContent;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
public class FileUploadController {

  private static final Logger logger = LoggerFactory
      .getLogger(FileUploadController.class);
  @Autowired
  private ImageService imageService;

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @PostMapping("/users/{userId}/picture")
  public void updatePhoto(@PathVariable Long userId,
      @RequestBody @NotNull @ValidContent MultipartFile file, @RequestHeader String Authorization)
      throws IOException {
    imageService.createImage(userId, file);
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @GetMapping("/users/{userId}/picture")
  public ResponseEntity<Resource> findPictureByUserId(
      HttpServletRequest request, @PathVariable Long userId, @RequestHeader String Authorization)
      throws IOException {
    Resource file = imageService.getProfilePicture(userId);
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
    } catch (IOException ex) {
      logger.info("Could not determine file type.");
    }
    if (contentType == null) {
      contentType = "application/octet-stream";
    }
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .body(new InputStreamResource(file.getInputStream()));
  }

  @PreAuthorize("@authenticationfacade.isAdministrator(#userId) or @authenticationfacade.isOwner(#userId)")
  @DeleteMapping("/users/{userId}/picture")
  public void deleteImage(@PathVariable Long userId, @RequestHeader String Authorization) throws IOException {
    imageService.deletePicture(userId);
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
