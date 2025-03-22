package com.eazybytes.accounts.unit.controllers;


import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.controller.AccountsController;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.service.IAccountsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerDeleteAccountTest {
    private Logger LOGGER = LoggerFactory.getLogger(AccountControllerDeleteAccountTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountsController accountsController;

    private MockMvc mockMvc;

    @MockitoBean
    private IAccountsService iAccountsService;

    @BeforeEach
    void setUp() {
        // Arrange
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Delete Valid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testFetchAccount_whenValidEmailProvided_returnsCorrectStatusCode() throws Exception {
        // Arrange
        String email = "Rolands@gmail.com";

        Mockito.when(iAccountsService.deleteAccount(email))
                .thenReturn(true);

        ResponseDto responseDto = new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/delete?email=" + email);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andReturn();

        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        ResponseDto fetchedResponse = new ObjectMapper().readValue(responseBodyAsString, ResponseDto.class);

        ResponseEntity<ResponseDto> fetchedDeleteResponse = ResponseEntity.ok(fetchedResponse);

        LOGGER.info("Response -> {}", fetchedResponse);


        // Assert
        Assertions.assertEquals(HttpStatus.OK, fetchedDeleteResponse.getStatusCode());
        Assertions.assertEquals("200", fetchedDeleteResponse.getBody().getStatusCode());
        Assertions.assertEquals("Request processed successfully", fetchedDeleteResponse.getBody().getStatusMsg());


    }

    @Test
    @DisplayName("Create InValid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testFetchAccount_whenInvalidEmailProvided_returnsError() throws Exception {
        // Arrange
        String email = "Rolands@gmail.com";
        ResourceNotFoundException resourceNotFoundException =  new ResourceNotFoundException("Account", "Email", email);

        Mockito.when(iAccountsService.deleteAccount(email))
                .thenThrow(resourceNotFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/delete?email=" + email)
                .contentType("application/json");

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andReturn();


        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        LOGGER.info("Response -> {}", responseBodyAsString);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(responseBodyAsString);

        // Assert
        Assertions.assertEquals("uri=/api/delete", jsonResponse.get("apiPath").asText());
        Assertions.assertEquals("NOT_FOUND", jsonResponse.get("errorCode").asText());
        Assertions.assertEquals("Account not found with the given input data Email : '"+email+"'", jsonResponse.get("errorMessage").asText());
        Assertions.assertNotNull(jsonResponse.get("errorTime")); // Ensure error timestamp exists
    }

    @Test
    @DisplayName("Create InValid Account : Check HttpStatus, JSON Status Code and Status Message")
    public void testFetchAccount_whenInvalidEmailProvided_returnsExpectationFailedException() throws Exception {
        // Arrange
        String email = "Rolands@gmail.com";

        Mockito.when(iAccountsService.deleteAccount(email))
                .thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/delete?email=" + email)
                .contentType("application/json");

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andReturn();
        LOGGER.info("Response -> {}", mvcResult.getResponse().getStatus());

        // Assert
        Assertions.assertEquals(417, mvcResult.getResponse().getStatus());
    }
}
