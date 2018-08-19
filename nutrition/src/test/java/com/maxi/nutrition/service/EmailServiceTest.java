package com.maxi.nutrition.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.maxi.nutrition.UserTestUtils;
import com.maxi.nutrition.model.User;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RunWith(JMockit.class)
@SpringBootTest
public class EmailServiceTest {

  @Tested
  private EmailService emailService;

  @Injectable
  private JavaMailSender mailSender;

  @Injectable
  private String confirmationMessage = "message";

  @Injectable
  private String url = "url";

  @Injectable
  private String confirmationSubject = "confirmationSubject";

  @Ignore
  @Test
  public void testSendEmail() {
    User user = UserTestUtils.createRandomUser();
    String token = RandomStringUtils.randomAlphanumeric(20);
    emailService.sendConfirmationEmail(user, token);
    String text = confirmationMessage + url + "?token=" + token;
    new Verifications() {{
      SimpleMailMessage message = null;
      mailSender.send(message = withCapture());
      assertThat(message.getSubject(), is(confirmationSubject));
      assertThat(message.getText(), is(text));
    }};
  }

}
