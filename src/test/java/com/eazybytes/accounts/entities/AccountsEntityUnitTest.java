package com.eazybytes.accounts.entities;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

// This annotation says to Spring to create context
// for beans that are related to
// Spring Data JPA Persistance Layer
// Put in context Entities and repositories
//@TestPropertySource(locations = "/application.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// This allows to run tests in parallel
// in aplication.properties we have server.port=0 -> means random
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestEntityManager
public class AccountsEntityUnitTest {

    /*
        This object is alternative to EntityManager
        This object is used to perform tests on Data layer
        It will allow us to persist information and then synchronize it with the database table
     */
    @Autowired
    private TestEntityManager testEntityManager;

    Accounts accountsEntity;

    @BeforeEach
    void setUp() {
        //Arrange
        accountsEntity = new Accounts();
        accountsEntity.setAccountNumber("account_" + System.currentTimeMillis());
        accountsEntity.setName("Rolands Bidzans");
        accountsEntity.setEmail("Rolandsnorigas@gmail.com");
        accountsEntity.setMobileNumber("12345678");
    }

    @Test
    @Transactional
    void testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserCustomerId() {
        //Act
        Accounts storedUserDetails = testEntityManager.persistAndFlush(accountsEntity);

        //Assert
        Assertions.assertEquals(storedUserDetails.getAccountNumber(), accountsEntity.getAccountNumber(), "User Account Number not same");
    }

    @Test
    @Transactional
    void testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserDetails() {
        //Act
        Accounts storedUserDetails = testEntityManager.persistAndFlush(accountsEntity);

        //Assert
        Assertions.assertEquals(storedUserDetails.getName(),accountsEntity.getName(), "User Name not same");
    }
}


//            Assertions.assertEquals(storedUserDetails.getFirstName(), storedUserDetails.getFirstName(), "User first name not same");
//        Assertions.assertEquals(storedUserDetails.getLastName(), storedUserDetails.getLastName(), "User last name not same");
//        Assertions.assertEquals(storedUserDetails.getEmail(), storedUserDetails.getEmail(), "User email not same");
//        Assertions.assertEquals(storedUserDetails.getEncryptedPassword(), storedUserDetails.getEncryptedPassword(), "User password not same");
