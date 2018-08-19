package com.maxi.nutrition.repository;

import static com.maxi.nutrition.UserTestUtils.createRandomUser;

import com.maxi.nutrition.model.Role;
import com.maxi.nutrition.model.User;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private TestEntityManager entityManager;


  @Test
  public void testFindByUserId() {
    User user = createRandomUser()
        .setAttempts(0)
        .setEnabled(false);
    entityManager.persist(user);

    Role role = new Role().setRole("ADMIN").setUser(user);
    Role role2 = new Role().setRole("USER").setUser(user);
    Role role3 = new Role().setRole("USER_MANAGER").setUser(user);
    entityManager.persist(role);
    entityManager.persist(role2);
    entityManager.persist(role3);

    List<Role> roles = roleRepository.findByUserId(user.getId());
    Assert.assertTrue(roles.size() == 3);
    Assert.assertTrue(roles.contains(role));
    Assert.assertTrue(roles.contains(role2));
    Assert.assertTrue(roles.contains(role3));
  }

}
