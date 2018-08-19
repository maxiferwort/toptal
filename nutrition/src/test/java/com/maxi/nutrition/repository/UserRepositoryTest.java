package com.maxi.nutrition.repository;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.maxi.nutrition.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testFindByUsername() {
    User user = createRandomUser()
        .setAttempts(0)
        .setEnabled(false);
    entityManager.persist(user);
    User db = userRepository.findByUsername(user.getUsername());
    assertEquals(user, db);
  }

  @Test
  public void testFindByUsername_returnsNull() {
    assertNull(userRepository.findByUsername(randomAlphanumeric(10)));
  }

  @Test
  public void testFindByEmail_returnsNull() {
    assertNull(userRepository.findByEmail(randomAlphanumeric(10)));
  }

  @Test
  public void testFindByEmail() {
    User user = createRandomUser().setAttempts(0).setEnabled(false);
    entityManager.persist(user);
    User db = userRepository.findByEmail(user.getEmail());
    assertEquals(user, db);
  }


}
