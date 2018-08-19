package com.maxi.nutrition.service;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.maxi.nutrition.model.User;
import com.maxi.nutrition.model.VerificationToken;
import com.maxi.nutrition.repository.UserRepository;
import com.maxi.nutrition.repository.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(JMockit.class)
@SpringBootTest
public class UserServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Tested
  private UserService userService;
  @Injectable
  private UserRepository userRepository;
  @Injectable
  private EmailService emailService;
  @Injectable
  private VerificationTokenRepository verificationTokenRepository;
  @Injectable
  private PasswordEncoder passwordEncoder;

  @Test
  public void testCreateUser() {
    final User user = createRandomUser();
    final String passwordEncoded = randomAlphanumeric(20);
    new Expectations() {{
      userRepository.save(withInstanceOf(User.class));
      returns(user.setId(1l));

      passwordEncoder.encode(anyString);
      returns(passwordEncoded);
    }};

    userService.createUser(user);

    new Verifications() {{
      User capture = null;
      userRepository.save(capture = withCapture());
      assertThat(capture.getUsername(), is(user.getUsername().toLowerCase()));
      assertThat(capture.getEmail(), is(user.getEmail().toLowerCase()));
      assertThat(capture.getEncodedPassword(), is(passwordEncoded));
      assertThat(capture.getEnabled(), is(true));
      assertThat(capture.getEmailConfirmation(), is(false));
      assertThat(capture.getAttempts(), is(0));
      assertNull(capture.getPicture());

      VerificationToken token = null;
      verificationTokenRepository.save(token = withCapture());
      times = 1;

      emailService.sendConfirmationEmail(withInstanceOf(User.class), token.getToken());
      times = 1;
    }};

  }

  @Test
  public void testUpdateUser() {
    final User user = createRandomUser();
    final String passwordEncoded = randomAlphanumeric(20);
    final User bd = createRandomUser()
        .setId(1l)
        .setEnabled(false)
        .setAttempts(0)
        .setEmailConfirmation(false);
    new Expectations() {{
      userRepository.findById(anyLong);
      returns(Optional.of(bd));

      passwordEncoder.encode(anyString);
      returns(passwordEncoded);
    }};

    userService.updateUser(user, 1l);

    new Verifications() {{
      User capture = null;
      userRepository.save(capture = withCapture());
      assertThat(capture.getUsername(), is(user.getUsername().toLowerCase()));
      assertThat(capture.getEmail(), is(user.getEmail().toLowerCase()));
      assertThat(capture.getEncodedPassword(), is(passwordEncoded));
      assertThat(capture.getEnabled(), is(false));
      assertThat(capture.getAttempts(), is(0));
      assertNull(capture.getPicture());
    }};
  }

  @Test
  public void testEnableUser() {
    String token = randomAlphanumeric(20);
    User user = createRandomUser().setId(1l).setAttempts(0).setEnabled(false);
    VerificationToken verificationToken = new VerificationToken().setToken(token).setExpiration(
        LocalDateTime.now().plusDays(1)).setUser(user).setId(1l);
    new Expectations() {{
      verificationTokenRepository
          .findByTokenAndExpirationAfter(anyString, withInstanceOf(LocalDateTime.class));
      returns(verificationToken);
    }};

    userService.confirmEmail(token);

    new Verifications() {{
      User capture = null;
      userRepository.save(capture = withCapture());
      assertTrue(user.getEmailConfirmation());

      verificationTokenRepository.delete(verificationToken);
      times = 1;
    }};
  }

  @Test
  public void testEnableUser_tokenDeleted() {
    String token = randomAlphanumeric(20);
    new Expectations() {{
      verificationTokenRepository
          .findByTokenAndExpirationAfter(anyString, withInstanceOf(LocalDateTime.class));
      returns(null);
    }};
    expectedException.expect(ResourceNotFoundException.class);
    userService.confirmEmail(token);
  }

  @Test
  public void testEnableUser_tokenExpired() {
    String token = randomAlphanumeric(20);
    User user = createRandomUser()
        .setId(1l)
        .setAttempts(0)
        .setEnabled(false);
    VerificationToken verificationToken = new VerificationToken()
        .setToken(token)
        .setExpiration(LocalDateTime.now().minusMinutes(1))
        .setUser(user)
        .setId(1l);
    new Expectations() {{
      verificationTokenRepository
          .findByTokenAndExpirationAfter(anyString, withInstanceOf(LocalDateTime.class));
      returns(verificationToken);
    }};
    expectedException.expect(ResourceNotFoundException.class);
    userService.confirmEmail(token);
  }

  @Test
  public void testFindById() {
    Long id = nextLong();
    final User user = createRandomUser()
        .setId(id)
        .setEnabled(false)
        .setAttempts(0)
        .setPicture("picture");
    new Expectations() {{
      userRepository.findById(anyLong);
      returns(Optional.of(user));
    }};

    User find = userService.findUserById(id);

    assertThat(find.getId(), is(id));
    assertThat(find.getUsername(), is(user.getUsername()));
    assertThat(find.getPassword(), is(user.getPassword()));
    assertThat(find.getEmail(), is(user.getEmail()));
    assertThat(find.getEnabled(), is(user.getEnabled()));
    assertThat(find.getAttempts(), is(user.getAttempts()));
    assertThat(find.getPicture(), is(user.getPicture()));
  }

  @Test
  public void testFindById_notFound() {
    new Expectations() {{
      userRepository.findById(anyLong);
      returns(Optional.empty());
    }};
    expectedException.expect(ResourceNotFoundException.class);
    userService.findUserById(nextLong());
  }

  @Test
  public void deleteById() {
    final User user = createRandomUser()
        .setId(nextLong());

    new Expectations() {{
      userRepository.findById(anyLong);
      returns(Optional.of(user));
    }};

    userService.deleteById(user.getId());

    new Verifications() {{
      User capture = null;
      userRepository.deleteById(user.getId());
      times = 1;
    }};
  }

  @Test
  public void testDeleteById_notFound() {
    new Expectations() {{
      userRepository.findById(anyLong);
      returns(Optional.empty());
    }};
    expectedException.expect(ResourceNotFoundException.class);
    userService.deleteById(nextLong());
  }
}
