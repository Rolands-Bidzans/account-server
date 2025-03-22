package com.eazybytes.accounts.integration.repositories;

import com.eazybytes.accounts.entities.Accounts;
import com.eazybytes.accounts.repositories.AccountsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.logging.Logger;

@SpringBootTest
public class AccountsRepositoryIntegrationTest {
    Logger LOGGER = Logger.getLogger(AccountsRepositoryIntegrationTest.class.getName());

    @Autowired
    AccountsRepository accountsRepository;

    String name = "John Doe";
    String email = "johndoe@example.com";
    String mobileNumber = "29878987";
    String accountNumber = "1234567890";

    @BeforeEach
    void setUp() {
        //Arrange
        Accounts accounts = new Accounts();
        accounts.setName(name);
        accounts.setEmail(email);
        accounts.setMobileNumber(mobileNumber);
        accounts.setAccountNumber(accountNumber);
        accountsRepository.save(accounts);
    }


    @Test
    void testFindByEmail_whenGivenCorrectEmail_returnsAccountEntity(){

        //Act
        Optional<Accounts> storedAccount = accountsRepository.findByEmail(email);

//        LOGGER.info(storedAccount.toString());

        //Assert
        Assertions.assertNotNull(storedAccount);
        Assertions.assertEquals(email, storedAccount.get().getEmail(),
                "Returned user email does not match the expected value");
        Assertions.assertEquals(accountNumber, storedAccount.get().getAccountNumber(),
                "Returned user accountNumber does not match the expected value");
        Assertions.assertEquals(name, storedAccount.get().getName(),
                "Returned user name does not match the expected value");
        Assertions.assertEquals(mobileNumber, storedAccount.get().getMobileNumber(),
                "Returned user mobileNumber does not match the expected value");

    }

    @Test
    void testFindByEmail_whenGivenIncorrectEmail_returnsAccountEntity(){

        //Act
        Optional<Accounts> storedAccount = accountsRepository.findByEmail("IncorrectEmail@gmail.com");

//        LOGGER.info(storedAccount.isEmpty() + "");

        //Assert
        Assertions.assertTrue(storedAccount.isEmpty());

    }

    @Test
    void deleteByAccountNumber_whenGivenCorrectAccountNumber_AccountIsNotPresent(){

        //Act
        accountsRepository.deleteByAccountNumber(accountNumber);

        Optional<Accounts> storedAccount = accountsRepository.findByEmail("IncorrectEmail@gmail.com");

        //Assert
        Assertions.assertTrue(storedAccount.isEmpty());

    }

    @Test
    void deleteByAccountNumber_whenGivenIncorrectAccountNumber_AccountStillPresent(){
        String invalidAccountNumber = "43242342342324";

        //Act
        accountsRepository.deleteByAccountNumber(invalidAccountNumber);

        Optional<Accounts> storedAccount = accountsRepository.findByEmail(email);

//        LOGGER.info(storedAccount.toString());

        //Assert
        Assertions.assertNotNull(storedAccount);
        Assertions.assertEquals(email, storedAccount.get().getEmail(),
                "Returned user email does not match the expected value");
        Assertions.assertEquals(accountNumber, storedAccount.get().getAccountNumber(),
                "Returned user accountNumber does not match the expected value");
        Assertions.assertEquals(name, storedAccount.get().getName(),
                "Returned user name does not match the expected value");
        Assertions.assertEquals(mobileNumber, storedAccount.get().getMobileNumber(),
                "Returned user mobileNumber does not match the expected value");

    }

}
