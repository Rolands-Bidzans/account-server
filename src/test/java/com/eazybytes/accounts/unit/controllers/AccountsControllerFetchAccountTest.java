package com.eazybytes.accounts.unit.controllers;


import com.eazybytes.accounts.controller.AccountsController;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.service.IAccountsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsControllerFetchAccountTest {
    private Logger LOGGER = LoggerFactory.getLogger( AccountsControllerFetchAccountTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountsController accountsController;

    private MockMvc mockMvc;

    @MockitoBean
    private IAccountsService iAccountsService;

    AccountsDto userAccountDto;

    @BeforeEach
    void setUp() {
        // Arrange
        userAccountDto = new AccountsDto();
        userAccountDto.setName("John");
        userAccountDto.setEmail("johndoe@example.com");
        userAccountDto.setMobileNumber("12345678");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @DisplayName("Create Valid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testFetchAccount_whenValidEmailProvided_returnsCorrectStatusCode() throws Exception {
        // Arrange
        AccountsDto returnedAccountDto = new ModelMapper().map(userAccountDto, AccountsDto.class);
        returnedAccountDto.setAccountNumber(UUID.randomUUID().toString());

        Mockito.when(iAccountsService.fetchAccount(userAccountDto.getEmail()))
                .thenReturn(returnedAccountDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/fetch?email=" + userAccountDto.getEmail())
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userAccountDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andReturn();


        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        AccountsDto fetchedUser = new ObjectMapper().readValue(responseBodyAsString, AccountsDto.class);

        ResponseEntity<AccountsDto> fetchedUserResponse = ResponseEntity.ok(fetchedUser);

        LOGGER.info("Response -> {}", fetchedUserResponse);
        // Assert
        Assertions.assertEquals(HttpStatus.OK, fetchedUserResponse.getStatusCode());
        Assertions.assertEquals(returnedAccountDto.getAccountNumber(), fetchedUserResponse.getBody().getAccountNumber());
        Assertions.assertEquals(returnedAccountDto.getEmail(), fetchedUserResponse.getBody().getEmail());
        Assertions.assertEquals(returnedAccountDto.getName(), fetchedUserResponse.getBody().getName());
        Assertions.assertEquals(returnedAccountDto.getMobileNumber(), fetchedUserResponse.getBody().getMobileNumber());

    }


    @Test
    @DisplayName("Create Valid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testFetchAccount_whenInvalidEmailProvided_returnsError() throws Exception {
        // Arrange
        ResourceNotFoundException resourceNotFoundException =  new ResourceNotFoundException("Account", "Email", userAccountDto.getEmail());

        Mockito.when(iAccountsService.fetchAccount(userAccountDto.getEmail()))
                .thenThrow(resourceNotFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/fetch?email=" + userAccountDto.getEmail())
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userAccountDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andReturn();


        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        LOGGER.info("Response -> {}", responseBodyAsString);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBodyAsString);

//        // Assert
        // Assertions: Verify JSON response structure
        Assertions.assertEquals("uri=/api/fetch", jsonResponse.get("apiPath").asText());
        Assertions.assertEquals("NOT_FOUND", jsonResponse.get("errorCode").asText());
        Assertions.assertEquals("Account not found with the given input data Email : '"+userAccountDto.getEmail()+"'", jsonResponse.get("errorMessage").asText());
        Assertions.assertNotNull(jsonResponse.get("errorTime")); // Ensure error timestamp exists
    }
}


//@GetMapping("/fetch")
//public ResponseEntity<AccountsDto> fetchAccountDetails(@RequestParam
//                                                       @Email(message = "Invalid email format")
//                                                       String email) {
//    AccountsDto customerDto = iAccountsService.fetchAccount(email);
//    return ResponseEntity.status(HttpStatus.OK).body(customerDto);
//}