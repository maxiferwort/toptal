package com.maxi.nutrition.service;

import com.maxi.nutrition.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

  private static String UPLOAD_ROOT = "upload-dir";

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private UserService userService;

  public Resource findImage(String filename) {
    return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
  }

  public void createImage(Long userId, MultipartFile file) throws IOException {
    Files.createDirectories(Paths.get(UPLOAD_ROOT));
    if (file != null && !file.isEmpty()) {
      User user = userService.findUserById(userId);
      String path = "IMAGE_USER_ID_" + userId;
      Files.deleteIfExists(Paths.get(UPLOAD_ROOT, path));
      Files.copy(file.getInputStream(),
          Paths.get(UPLOAD_ROOT, path));
    }
  }

  public void deletePicture(Long userId) throws IOException {
    String path = "IMAGE_USER_ID_" + userId;
    Files.deleteIfExists(Paths.get(UPLOAD_ROOT, path));
  }

  public Resource getProfilePicture(Long userId) {
    return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/IMAGE_USER_ID_" + userId);
  }

}
