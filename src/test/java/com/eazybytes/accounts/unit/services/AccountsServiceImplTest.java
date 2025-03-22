package com.eazybytes.accounts.unit.services;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entities.Accounts;
import com.eazybytes.accounts.exception.AccountAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.repositories.AccountsRepository;
import com.eazybytes.accounts.service.impl.AccountsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsServiceImplTest {

    @MockitoBean
    private AccountsRepository accountsRepository;

    @InjectMocks
    private AccountsServiceImpl accountServiceImpl;

    private String initialAccountNumber = UUID.randomUUID().toString();
    private String initialAccountEmail = "Ak@example.com";
    private String initialAccountMobileNumber = "12345678";
    private String initialName = "Rolands Bidzans";

    private String updatedAccountMobileNumber = "12345678";
    private String updatedName = "Andrejs Kovancovs";

    private String noneExistingAccountEmail = "RB@example.com";

    private Accounts initialAccount;
    private Accounts updatedAccount;
    private AccountsDto initialAccountDto;
    AccountsDto updatedAccountDto;

    @BeforeEach
    void setUp() {
        // Arrange

        // ENTITY INITIAL
        initialAccount = new Accounts();
        initialAccount.setAccountNumber(initialAccountNumber);
        initialAccount.setEmail(initialAccountEmail);
        initialAccount.setName(initialName);
        initialAccount.setMobileNumber(initialAccountMobileNumber);

        // ENTITY UPDATED
        updatedAccount = new Accounts();
        updatedAccount.setAccountNumber(initialAccountNumber);
        updatedAccount.setEmail(initialAccountEmail);
        updatedAccount.setName(updatedName);
        updatedAccount.setMobileNumber(updatedAccountMobileNumber);

        // Account DTO INITIAL
        initialAccountDto = new AccountsDto();
        initialAccountDto.setAccountNumber(initialAccountNumber);
        initialAccountDto.setEmail(initialAccountEmail);
        initialAccountDto.setName(initialName);
        initialAccountDto.setMobileNumber(initialAccountMobileNumber);

        // Account DTO UPDATED
        updatedAccountDto = new AccountsDto();
        updatedAccountDto.setAccountNumber(initialAccountNumber);
        updatedAccountDto.setEmail(initialAccountEmail);
        updatedAccountDto.setName(updatedName);
        updatedAccountDto.setMobileNumber(updatedAccountMobileNumber);
    }

    @Test
    void testCreateAccount_Success() {

        // Mock behavior: No existing account found
        Mockito.when(accountsRepository.findByEmail(initialAccount.getEmail())).thenReturn(Optional.empty());

        // Call the service method
        accountServiceImpl.createAccount(initialAccountDto);

        // Verify the repository method was called
        Mockito.verify(accountsRepository).save(Mockito.any(Accounts.class));
        Mockito.verify(accountsRepository, Mockito.times(1)).findByEmail(initialAccount.getEmail());
    }

    @Test
    void testCreateAccount_ThrowsException_WhenEmailAlreadyExists() {
        // Mock behavior: Email already exists
        Mockito.when(accountsRepository.findByEmail(initialAccount.getEmail())).thenReturn(Optional.of(initialAccount));

        // Assert that exception is thrown
        Exception exception = Assertions.assertThrows(AccountAlreadyExistsException.class, () ->
                accountServiceImpl.createAccount(initialAccountDto)
        );

        // Verify the exception message
        Assertions.assertEquals("Account already registered with given Email " + initialAccount.getEmail() + "", exception.getMessage());

        // Verify repository calls (save should NOT be called)
        Mockito.verify(accountsRepository, Mockito.times(1)).findByEmail(initialAccount.getEmail());
        Mockito.verify(accountsRepository, Mockito.never()).save(Mockito.any(Accounts.class));
    }

    @Test
    void testFetchAccount_Success()  {
        // Mock behavior: Account is found
        Mockito.when(accountsRepository.findByEmail(initialAccount.getEmail())).thenReturn(Optional.of(initialAccount));

        // Call the Delete account method
        AccountsDto result = accountServiceImpl.fetchAccount(initialAccount.getEmail());

        // Arrange AccountDto
        Assertions.assertEquals(initialAccount.getAccountNumber(), result.getAccountNumber());
        Assertions.assertEquals(initialAccount.getName(), result.getName());
        Assertions.assertEquals(initialAccount.getEmail(), result.getEmail());
        Assertions.assertEquals(initialAccount.getMobileNumber(), result.getMobileNumber());
    }

    @Test
    void testFetchAccount_ThrowsException_WhenEmailDoesNotExists() {
        // Mock behavior: Email does not exist
        Mockito.when(accountsRepository.findByEmail(noneExistingAccountEmail)).thenReturn(Optional.empty());

        // Assert that exception is thrown
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                accountServiceImpl.fetchAccount(noneExistingAccountEmail)
        );

        // Verify the exception message
        Assertions.assertEquals("Account not found with the given input data Email : '"+ noneExistingAccountEmail +"'", exception.getMessage());

        // Verify repository calls (save should NOT be called)
        Mockito.verify(accountsRepository, Mockito.times(1)).findByEmail(noneExistingAccountEmail);
    }

    @Test
    void testUpdateAccount_Success()  {

        // Mock behavior: Account is found
        Mockito.when(accountsRepository.findByEmail(initialAccount.getEmail())).thenReturn(Optional.of(updatedAccount));
        Mockito.when(accountsRepository.save(updatedAccount)).thenReturn(updatedAccount);

        // Call the Update account method
        boolean result = accountServiceImpl.updateAccount(updatedAccountDto);

        // Assert the result
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateAccount_ThrowsException_WhenAccountNotFound() {
        // Arrange AccountDto
       updatedAccountDto.setEmail(noneExistingAccountEmail);

        // Mock behavior: Account is found
        Mockito.when(accountsRepository.findByEmail(updatedAccountDto.getEmail())).thenReturn(Optional.empty());

        // Assert that exception is thrown
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                accountServiceImpl.updateAccount(updatedAccountDto)
        );

        // Verify the exception message
        Assertions.assertEquals("Account not found with the given input data Email : '" + updatedAccountDto.getEmail() + "'", exception.getMessage());

        // Verify repository calls (save should NOT be called)
        Mockito.verify(accountsRepository, Mockito.times(1)).findByEmail(updatedAccountDto.getEmail());
    }


    @Test
    void testDeleteAccount_Success()  {
        // Mock behavior: Account is found
        Mockito.when(accountsRepository.findByEmail(initialAccount.getEmail())).thenReturn(Optional.of(initialAccount));

        // Call the Delete account method
        boolean result = accountServiceImpl.deleteAccount(initialAccount.getEmail());

        // Assert the result
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteAccount_ThrowsException_WhenAccountNotFound() {
        // Mock behavior: Account is NOT found
        Mockito.when(accountsRepository.findByEmail(noneExistingAccountEmail)).thenReturn(Optional.empty());

        // Assert that calling deleteAccount throws ResourceNotFoundException
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                accountServiceImpl.deleteAccount(noneExistingAccountEmail)
        );

        // Verify the exception message
        Assertions.assertEquals("Account not found with the given input data email : '" + noneExistingAccountEmail + "'", exception.getMessage());
    }


}

