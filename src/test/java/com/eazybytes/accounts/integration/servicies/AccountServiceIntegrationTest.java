package com.eazybytes.accounts.integration.servicies;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entities.Accounts;
import com.eazybytes.accounts.repositories.AccountsRepository;
import com.eazybytes.accounts.service.impl.AccountsServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceIntegrationTest {
    @Autowired
    private AccountsServiceImpl accountsServiceImpl;

    @Autowired
    private AccountsRepository accountsRepository;

    private String accountNumber1 = UUID.randomUUID().toString();
    private String email1 = "RB@example.com";

    private String accountNumber2 = UUID.randomUUID().toString();
    private String email2 = "Ak@example.com";


    @Test
    @Order(1)
    void testDeleteAccountByEmail() {
        // Arrange & Save
        Accounts user1 = new Accounts();
        user1.setAccountNumber(accountNumber2);
        user1.setEmail(email2);
        user1.setName("Rolands Bidzans");
        user1.setMobileNumber("12345678");
        accountsRepository.save(user1);

        // Act: Delete the account
        accountsServiceImpl.deleteAccount(email2);

        // Assert: Verify deletion
        Assertions.assertFalse(accountsRepository.findByEmail(email2).isPresent());
    }


    @Test
    @Order(2)
    void testFetchAccount() {
        // Arrange & Save
        Accounts user1 = new Accounts();
        user1.setAccountNumber(accountNumber1);
        user1.setEmail(email1);
        user1.setName("Rolands Bidzans");
        user1.setMobileNumber("12345678");
        accountsRepository.save(user1);


        AccountsDto accountsDto = accountsServiceImpl.fetchAccount(email1);

        Assertions.assertNotNull(accountsDto);
        Assertions.assertEquals("Rolands Bidzans", accountsDto.getName());
        Assertions.assertEquals("12345678", accountsDto.getMobileNumber());
        Assertions.assertEquals(email1, accountsDto.getEmail());
        Assertions.assertEquals(accountNumber1, accountsDto.getAccountNumber());
    }

    @Test
    @Order(3)
    void testUpdateAccount() {
        // Updated Account
        AccountsDto userDto = new AccountsDto();
        userDto.setAccountNumber(accountNumber1);
        userDto.setEmail(email1);
        userDto.setName("Andrejs Kopnins");
        userDto.setMobileNumber("23454434");

        // Act: Update the account
        accountsServiceImpl.updateAccount(userDto);

        // Assert: Verify deletion
        Optional<Accounts> updatedAccount = accountsRepository.findByEmail(email1);

        Assertions.assertTrue(updatedAccount.isPresent());
        Assertions.assertEquals("Andrejs Kopnins", updatedAccount.get().getName());
        Assertions.assertEquals("23454434", updatedAccount.get().getMobileNumber());
        Assertions.assertEquals(email1, updatedAccount.get().getEmail());
//        Assertions.assertEquals(accountNumber1, updatedAccount.get().getAccountNumber());
    }



//public AccountsDto fetchAccount(String email) {
//public void createAccount(AccountsDto accountDto) {
}

