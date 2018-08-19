package com.maxi.nutrition.service;

import com.maxi.nutrition.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


  @Autowired
  private JavaMailSender mailSender;

  @Value("${email.confirmation.message}")
  private String confirmationMessage;

  @Value("${app.confirm.url}")
  private String url;

  @Value("${app.base.url}")
  private String baseUrl;

  @Value("${email.confirmation.subject}")
  private String confirmationSubject;

  @Value("${email.invite.subject}")
  private String inviteSubject;

  @Value("${email.invite.message}")
  private String inviteMessage;

  @Value("${app.send.email.debug}")
  private Boolean debug;

  @Async
  public void sendConfirmationEmail(User user, String token) {
    if (!debug) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setSubject(confirmationSubject);
      String text = confirmationMessage + url + "?token=" + token;
      message.setText(text);
      message.setTo(user.getEmail());
      message.setFrom("no-reply@nutrition.com");
      mailSender.send(message);
    }
  }


  @Async
  public void sendInvitationEmail(String email) {
    if (!debug) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setSubject(inviteSubject);
      String text = inviteMessage + baseUrl + "/register?email=" + email;
      message.setText(text);
      message.setTo(email);
      message.setFrom("no-reply@nutrition.com");
      mailSender.send(message);
    }
  }
}
