package com.eazybytes.accounts.integration.controllers;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.entities.Accounts;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.repositories.AccountsRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsControllerIntegrationTest {
    private Logger LOGGER = LoggerFactory.getLogger(AccountsControllerIntegrationTest.class);

    @Autowired
    private TestRestTemplate testRestTemplate; // Replaces mocked WebClient

    @MockitoBean
    private AccountsRepository accountsRepository;

    String email = "johndoe@example.com";
    String mobileNumber = "12345678";
    String name = "John Doe";
    String accountNumber = "2213123456";

    AccountsDto accountDto;

    @BeforeEach
    public void setUp() {
        // Arrange
        accountDto = new AccountsDto();
        accountDto.setName(name);
        accountDto.setEmail(email);
        accountDto.setMobileNumber(mobileNumber);
        accountDto.setAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Create New Valid Account")
    @Order(1)
    public void testCreateAccount_whenValidDetailsProvided_returnsCorrectStatusCode() throws Exception {
        // Arrange
        Accounts accounts = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior: No existing account found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(accountsRepository.save(Mockito.any(Accounts.class))).thenReturn(accounts);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<AccountsDto> requestEntity = new HttpEntity<>(accountDto, headers);

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/create",
                HttpMethod.POST,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<ResponseDto>() {});

        LOGGER.info(String.valueOf(response));

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Account created successfully", response.getBody().getStatusMsg());
    }

    @Test
    @DisplayName("Create The same account (duplicate): ERROR")
    @Order(2)
    public void testCreateAccount_whenExistingUserDetailsProvided_returns400() throws Exception {
        // Arrange
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior: No existing account found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(account));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // Use HttpEntity with the correct body (orderDto)
        HttpEntity<AccountsDto> requestEntity = new HttpEntity<>(accountDto, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/create",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {}  // Expecting JSON response as String
        );

        // Print error response for debugging
//        LOGGER.info(String.valueOf(response));

        // Assert that the response status is 400 Bad Request
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("Account already registered with given Email"));
    }

    @Test
    @DisplayName("Fetch valid account")
    @Order(3)
    public void testFetchAccount_whenValidDetailsProvided_returnsCorrectStatusCode() throws Exception {

        // Arrange
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior: No existing account found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(account));

        // Act
        ResponseEntity<AccountsDto> response = testRestTemplate.exchange(
                "/api/fetch?email=" + email,
                HttpMethod.GET,  // Change to POST since your API expects POST
                null,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<AccountsDto>() {});

//        LOGGER.info(String.valueOf(response));

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(name, response.getBody().getName());
        Assertions.assertEquals(email, response.getBody().getEmail());
        Assertions.assertEquals(mobileNumber, response.getBody().getMobileNumber());

        accountNumber = response.getBody().getAccountNumber();
    }

    @Test
    @Order(4)
    @DisplayName("Fetch Invalid Account")
    public void testFetchAccount_whenInvalidDetailsProvided_returnsError() throws Exception {

        // Mock behavior: No existing account found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/fetch?email=" + email,
                HttpMethod.GET,  // Change to POST since your API expects POST
                null,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<String>() {});


        // Assert
//        LOGGER.info(response.getBody());

        // Assert that the response status is 400 Bad Request
        Assertions.assertEquals(HttpStatus.NOT_FOUND , response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("Account not found with the given input data Email : '" + email + "'"));
        Assertions.assertTrue(response.getBody().contains("uri=/api/fetch"));
    }


    @Test
    @DisplayName("Update Valid Account")
    @Order(5)
    public void testUpdateAccount_whenValidDetailsProvided_returnsCorrectStatusCode() throws Exception {
        // Arrange
        accountDto.setName("Rolands Bidzans");
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior:
        // Account with provided email is found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(account));
        // Update account in repository
        Mockito.when(accountsRepository.save(Mockito.any(Accounts.class))).thenReturn(account);

        // Create a HttpEntity with the correct body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<AccountsDto> requestEntity = new HttpEntity<>(accountDto, headers);

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/update?email=" + email,
                HttpMethod.PUT,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<ResponseDto>() {});

        //LOGGER.info(String.valueOf(response));

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Request processed successfully", response.getBody().getStatusMsg());
    }

    @Test
    @DisplayName("Update non existing Account")
    @Order(6)
    public void testUpdateAccount_whenInvalidDetailsProvided_returnsCorrectStatusCode() throws Exception {
        // Arrange
        accountDto.setName("Rolands Bidzans");

        // Mock behavior:
        // Account with provided not found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // Create a HttpEntity with the correct body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<AccountsDto> requestEntity = new HttpEntity<>(accountDto, headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/update?email=" + email,
                HttpMethod.PUT,  // Change to POST since your API expects POST
                requestEntity,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<String>() {});

//        LOGGER.info(response.getBody());

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND , response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("Account not found with the given input data Email : '" + email + "'"));
        Assertions.assertTrue(response.getBody().contains("uri=/api/update"));
    }

    @Test
    @Order(7)
    @DisplayName("Delete existing Account")
    public void testDeleteAccount_whenValidDetailsProvided_returnsSuccess() throws Exception {
        // Arrange
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior:
        // Account with provided email is found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(account));
        // Delete account in repository
        Mockito.doNothing().when(accountsRepository).deleteByAccountNumber(Mockito.anyString());

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/delete?email=" + email,
                HttpMethod.DELETE,  // Change to POST since your API expects POST
                null,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<ResponseDto>() {
                });


        // Assert
        // LOGGER.info(String.valueOf(response));

        // Assert that the response status is 400 Bad Request
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Request processed successfully", response.getBody().getStatusMsg());
    }

    @Test
    @Order(8)
    @DisplayName("Delete non existing Account")
    public void testDeleteAccount_whenInvalidDetailsProvided_returnsError() throws Exception {
        // Arrange
        Accounts account = AccountsMapper.mapToAccounts(accountDto, new Accounts());

        // Mock behavior:
        // Account with provided email is found
        Mockito.when(accountsRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ResponseDto> response = testRestTemplate.exchange(
                "/api/delete?email=" + email,
                HttpMethod.DELETE,  // Change to POST since your API expects POST
                null,    // Pass HttpEntity with orderDto as body
                new ParameterizedTypeReference<ResponseDto>() {
                });


        // Assert
         LOGGER.info(String.valueOf(response));

        // Assert that the response status is 404 Not Found Request
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



}
