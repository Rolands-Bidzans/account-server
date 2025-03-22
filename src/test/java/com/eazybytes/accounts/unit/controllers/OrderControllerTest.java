package com.eazybytes.accounts.unit.controllers;

import com.eazybytes.accounts.constants.OrdersConstants;
import com.eazybytes.accounts.controller.OrderController;
import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Set;
import java.util.logging.Logger;

@SpringBootTest
public class OrderControllerTest {

    Logger LOGGER = Logger.getLogger(OrderControllerTest.class.getName());

    private MockMvc mockMvc;

    @MockitoBean
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    OrderController orderController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    OrdersDto orderDto = new OrdersDto();

    @BeforeEach
    void setUp() {
        // Arrange
        orderDto.setName("Test Order");
        orderDto.setQty(2);
        orderDto.setPrice(50.99);
        orderDto.setStatus("Pending");
        orderDto.setAccountNumber("123456789");
    }

    @Test
    public void testCreateOrder() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("201");
        responseDto.setStatusMsg("Order created successfully");

        Mockito.when(orderServiceImpl.createOrder(Mockito.anyString(), Mockito.any(OrdersDto.class)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = orderController.createOrder("email@inbox.lv", orderDto);

        // Verify the response
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(OrdersConstants.STATUS_201, response.getBody().getStatusCode());
        Assertions.assertEquals(OrdersConstants.MESSAGE_201, response.getBody().getStatusMsg());
    }

    @Test
    public void testCreateOrder_returnWebClientResponseExceptionBadRequest() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("201");
        responseDto.setStatusMsg("Order created successfully");

        WebClientResponseException badRequestException = WebClientResponseException
                .create(HttpStatus.BAD_REQUEST.value(), "Bad Request", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.createOrder(Mockito.anyString(), Mockito.any(OrdersDto.class)))
                .thenThrow(badRequestException);

        ResponseEntity<ResponseDto> response = orderController.createOrder("email@inbox.lv", orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Order placement failed: Invalid order details.", response.getBody().getStatusMsg());
        Assertions.assertEquals("400", response.getBody().getStatusCode());
    }

    @Test
    public void testCreateOrder_returnWebClientResponseExceptionInternalServerError() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("201");
        responseDto.setStatusMsg("Order created successfully");

        WebClientResponseException internalServerErrorException = WebClientResponseException
                .create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.createOrder(Mockito.anyString(), Mockito.any(OrdersDto.class)))
                .thenThrow(internalServerErrorException);

        ResponseEntity<ResponseDto> response = orderController.createOrder("email@inbox.lv", orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Order could not be placed due to an internal error.", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testCreateOrder_returnExceptionResponseExceptionInternalServerError() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("201");
        responseDto.setStatusMsg("Order created successfully");

        Mockito.when(orderServiceImpl.createOrder(Mockito.anyString(), Mockito.any(OrdersDto.class)))
                .thenThrow(new NullPointerException("Unexpected null value"));

        ResponseEntity<ResponseDto> response = orderController.createOrder("email@inbox.lv", orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Order could not be placed due to an internal error.", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void fetchAllOrders() {

        Mockito.when(orderServiceImpl.fetchAllOrders(Mockito.anyString()))
                .thenReturn(Set.of(orderDto));

        ResponseEntity<?> response = orderController.fetchAllOrders("213123123");

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testfetchAllOrders_returnWebClientResponseExceptionBadRequest() {

        WebClientResponseException notFoundException = WebClientResponseException
                .create(HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.fetchAllOrders(Mockito.anyString()))
                .thenThrow(notFoundException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchAllOrders("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("No orders found for the given criteria.", response.getBody().getStatusMsg());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
    }


    @Test
    public void testFetchAllOrders_returnWebClientResponseExceptionInternalServerError() {

        WebClientResponseException internalServerErrorException = WebClientResponseException
                .create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.fetchAllOrders(Mockito.anyString()))
                .thenThrow(internalServerErrorException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchAllOrders("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Fetch All operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testFetchAllOrders_returnExceptionResponseExceptionInternalServerError() {

        Mockito.when(orderServiceImpl.fetchAllOrders(Mockito.anyString()))
                .thenThrow(new NullPointerException("Unexpected null value"));

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchAllOrders("213123123");

        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Fetch All operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void fetchOrder() {

        Mockito.when(orderServiceImpl.fetchOrder(Mockito.anyString()))
                .thenReturn(orderDto);

        ResponseEntity<?> response = orderController.fetchOrder("213123123");

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFetchOrder_returnWebClientResponseExceptionBadRequest() {

        WebClientResponseException notFoundException = WebClientResponseException
                .create(HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.fetchOrder(Mockito.anyString()))
                .thenThrow(notFoundException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchOrder("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Order not found for the given ID.", response.getBody().getStatusMsg());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
    }


    @Test
    public void testFetchOrder_returnWebClientResponseExceptionInternalServerError() {

        WebClientResponseException internalServerErrorException = WebClientResponseException
                .create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.fetchOrder(Mockito.anyString()))
                .thenThrow(internalServerErrorException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchOrder("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Fetch operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testFetchOrder_returnExceptionResponseExceptionInternalServerError() {

        Mockito.when(orderServiceImpl.fetchOrder(Mockito.anyString()))
                .thenThrow(new NullPointerException("Unexpected null value"));

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.fetchOrder("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Fetch operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testUpdateOrder() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("200");
        responseDto.setStatusMsg("Order updated successfully");

        Mockito.when(orderServiceImpl.updateOrderDetails(Mockito.any(OrdersDto.class)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = orderController.updateOrderDetails(orderDto);

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(OrdersConstants.STATUS_200, response.getBody().getStatusCode());
        Assertions.assertEquals(OrdersConstants.MESSAGE_200_UPDATE, response.getBody().getStatusMsg());
    }

    @Test
    public void testUpdateOrder_returnWebClientResponseExceptionBadRequest() {

        WebClientResponseException notFoundException = WebClientResponseException
                .create(HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.updateOrderDetails(Mockito.any(OrdersDto.class)))
                .thenThrow(notFoundException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.updateOrderDetails(orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Order to update not found.", response.getBody().getStatusMsg());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
    }


    @Test
    public void testUpdateOrder_returnWebClientResponseExceptionInternalServerError() {

        WebClientResponseException internalServerErrorException = WebClientResponseException
                .create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.updateOrderDetails(Mockito.any(OrdersDto.class)))
                .thenThrow(internalServerErrorException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.updateOrderDetails(orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Update operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testUpdateOrder_returnExceptionResponseExceptionInternalServerError() {

        Mockito.when(orderServiceImpl.updateOrderDetails(Mockito.any(OrdersDto.class)))
                .thenThrow(new NullPointerException("Unexpected null value"));

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.updateOrderDetails(orderDto);

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Update operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testDeleteOrder() {

        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatusCode("200");
        responseDto.setStatusMsg("Order deleted successfully");

        Mockito.when(orderServiceImpl.deleteOrderDetails(Mockito.anyString()))
                .thenReturn(responseDto);

        ResponseEntity<ResponseDto> response = orderController.deleteOrderDetails("213123123");

        // Verify the response
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(OrdersConstants.STATUS_200, response.getBody().getStatusCode());
        Assertions.assertEquals(OrdersConstants.MESSAGE_200_DELETE, response.getBody().getStatusMsg());
    }

    @Test
    public void testDeleteOrder_returnWebClientResponseExceptionBadRequest() {

        WebClientResponseException notFoundException = WebClientResponseException
                .create(HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.deleteOrderDetails(Mockito.anyString()))
                .thenThrow(notFoundException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.deleteOrderDetails("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Order to delete not found.", response.getBody().getStatusMsg());
        Assertions.assertEquals("404", response.getBody().getStatusCode());
    }


    @Test
    public void testDeleteOrder_returnWebClientResponseExceptionInternalServerError() {

        WebClientResponseException internalServerErrorException = WebClientResponseException
                .create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);

        Mockito.when(orderServiceImpl.deleteOrderDetails(Mockito.anyString()))
                .thenThrow(internalServerErrorException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.deleteOrderDetails("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Delete operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testDeleteOrder_returnExceptionResponseExceptionInternalServerError() {

        Mockito.when(orderServiceImpl.deleteOrderDetails(Mockito.anyString()))
                .thenThrow(new NullPointerException("Unexpected null value"));

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.deleteOrderDetails("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Delete operation failed. Please try again or contact Dev team", response.getBody().getStatusMsg());
        Assertions.assertEquals("500", response.getBody().getStatusCode());
    }

    @Test
    public void testDeleteOrder_returnExceptionResponseExpectionFailedError() {

        WebClientResponseException expectationFailedErrorException = WebClientResponseException
                .create(HttpStatus.EXPECTATION_FAILED.value(), "Internal Server Error", HttpHeaders.EMPTY, null, null);


        Mockito.when(orderServiceImpl.deleteOrderDetails(Mockito.anyString()))
                .thenThrow(expectationFailedErrorException);

        ResponseEntity<ResponseDto> response = (ResponseEntity<ResponseDto>) orderController.deleteOrderDetails("213123123");

//        LOGGER.info(response.toString());

        // Assert
        Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        Assertions.assertEquals("Delete operation failed. Please try again or contact the Dev team.", response.getBody().getStatusMsg());
        Assertions.assertEquals("417", response.getBody().getStatusCode());
    }

}
