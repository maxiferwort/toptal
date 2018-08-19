package com.maxi.nutrition.repository;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.maxi.nutrition.UserTestUtils;
import com.maxi.nutrition.model.User;
import com.maxi.nutrition.model.VerificationToken;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VerificationTokenRepositoryTest {

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testFindByToken() {
    User user = UserTestUtils.createRandomUser().setEnabled(false).setAttempts(0);
    entityManager.persist(user);
    VerificationToken verificationToken = new VerificationToken()
        .setToken(RandomStringUtils.randomAlphanumeric(20)).setExpiration(
            LocalDateTime.now()).setUser(user);
    entityManager.persist(verificationToken);
    VerificationToken db = verificationTokenRepository.findByToken(verificationToken.getToken());
    assertEquals(db, verificationToken);
  }

  @Test
  public void testFindByUser() {
    User user = UserTestUtils.createRandomUser().setEnabled(false).setAttempts(0);
    entityManager.persist(user);
    VerificationToken verificationToken = new VerificationToken()
        .setToken(RandomStringUtils.randomAlphanumeric(20)).setExpiration(
            LocalDateTime.now()).setUser(user);
    entityManager.persist(verificationToken);
    VerificationToken verificationToken2 = new VerificationToken()
        .setToken(RandomStringUtils.randomAlphanumeric(20)).setExpiration(
            LocalDateTime.now()).setUser(user);
    entityManager.persist(verificationToken2);

    List<VerificationToken> tokens = verificationTokenRepository.findByUserId(user.getId());
    assertTrue(tokens.size() == 2);
    assertTrue(tokens.contains(verificationToken));
    assertTrue(tokens.contains(verificationToken2));
  }


}
