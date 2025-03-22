package com.eazybytes.accounts.unit.controllers;

import com.eazybytes.accounts.controller.OrderController;
import com.eazybytes.accounts.dto.NotificationEvent;
import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.publisher.NotificationProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// This allows to run tests in parallel
// in aplication.properties we have server.port=0 -> means random
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerCreateOrderTest {
    private Logger LOGGER = LoggerFactory.getLogger(OrderControllerCreateOrderTest.class);

    private MockMvc mockMvc;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    private NotificationProducer notificationProducer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderController orderController;

    OrdersDto orderDto;
    NotificationEvent event;

    @BeforeEach
    void setUp() {
        // Arrange
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

//    @Test
//    public void testPlaceOrder_whenValidDataProvided_returnsCreatedStatus() throws Exception {
//        // Arrange
//        OrdersDto orderDto = new OrdersDto();
//        orderDto.setName("Test Order");
//        orderDto.setQty(2);
//        orderDto.setPrice(45.99);
//        orderDto.setStatus("Pending");
//        orderDto.setAccountNumber("123456789");
//
//        String receiverEmail = "rolandsnorigaS@gmail.com";
//
//        // Mock WebClient response for the external service call (no actual HTTP request will be made)
//        Mockito.when(webClient.post()).thenReturn(Mockito.mock(WebClient.RequestBodyUriSpec.class));
//        Mockito.when(webClient.post().uri(Mockito.anyString())).thenReturn(Mockito.mock(WebClient.RequestBodySpec.class));
//        Mockito.when(webClient.post().uri(Mockito.anyString()).bodyValue(Mockito.any(OrdersDto.class)))
//                .thenReturn(Mockito.mock(WebClient.RequestHeadersSpec.class));
//
//        // Mock sending the notification message to the producer (no actual message will be sent)
//        Mockito.doNothing().when(notificationProducer).sendMessage(Mockito.any(NotificationEvent.class));
//
//        // Act
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/create")
//                .param("receiverEmail", receiverEmail)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(orderDto));
//
//        MvcResult mvcResult = mockMvc.perform(requestBuilder)
//                .andReturn();
//
//        // Assert
//        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
//        System.out.println("Response: " + responseBodyAsString);
//
//        // Assert the response status and the returned JSON
//        mockMvc.perform(requestBuilder)
//                .andExpect(MockMvcResultMatchers.status().isCreated())  // Assert status 201 Created
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value("404"))  // Assert statusCode field
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusMsg").value("Order created successfully"));  // Assert statusMsg field
//    }

//    @Test
//    public void testPlaceOrder_whenValidDataProvided_returnsCreatedStatus() throws Exception {
//        // Arrange
//        OrdersDto orderDto = new OrdersDto();
//        orderDto.setName("Test Order");
//        orderDto.setQty(2);
//        orderDto.setPrice(50.99);
//        orderDto.setStatus("Pending");
//        orderDto.setAccountNumber("123456789");
//
//        String receiverEmail = "test@example.com";
//
//        // Act
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/api/orders/create")
//                .param("receiverEmail", receiverEmail)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(orderDto));
//
//        // Perform the request and capture the result
//        MvcResult mvcResult = mockMvc.perform(requestBuilder)
//                .andReturn();
//
//        // Mock NotificationProducer (just prevent real execution)
//        Mockito.doNothing().when(notificationProducer).sendMessage(Mockito.any(NotificationEvent.class));
//
//
//        // Assert
//        mockMvc.perform(requestBuilder)
//                .andExpect(MockMvcResultMatchers.status().isCreated())  // Expect status 201
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value("201"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.statusMsg").value("Order created successfully"));
//    }

    @Test
    public void testPlaceOrder_WhenValidInput_ExpectCreated() throws Exception {
        // Arrange
        OrdersDto orderDto = new OrdersDto();
        orderDto.setName("Test Order");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("Pending");
        orderDto.setAccountNumber("123456789");

        String receiverEmail = "test@example.com";

        // Mock WebClient behavior
        WebClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.anyString())).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(Mockito.any(OrdersDto.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Mock NotificationProducer (just prevent real execution)
        Mockito.doNothing().when(notificationProducer).sendMessage(Mockito.any(NotificationEvent.class));

        // When (sending the request)
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/orders/create?receiverEmail=" + receiverEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())  // Expect 201
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value("201"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusMsg").value("Order created successfully"))
                .andReturn();

    }

}
