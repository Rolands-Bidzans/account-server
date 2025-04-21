package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.OrdersConstants;
import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.publisher.NotificationProducer;
import com.eazybytes.accounts.service.IOrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Set;


@RestController
@RequestMapping(path="/api/orders", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    WebClient webClient;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createOrder(@RequestParam String receiverEmail,
                                                  @Valid @RequestBody OrdersDto orderDto) {
        try {
            ResponseDto response = iOrderService.createOrder(receiverEmail, orderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDto(OrdersConstants.STATUS_400, OrdersConstants.MESSAGE_400_CREATE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE));
        }
    }



    @GetMapping("/fetchAll")
    public ResponseEntity<?> fetchAllOrders(@RequestParam String accountNumber) {
        try {
            Set<OrdersDto> orders = iOrderService.fetchAllOrders(accountNumber);
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_FETCH_ALL));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH_ALL));
        }
        catch (Exception e) {  // Catch any other unexpected errors

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH_ALL));
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchOrder(@RequestParam String orderId) {

        try {
            OrdersDto ordersDto = iOrderService.fetchOrder(orderId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ordersDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_FETCH));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_FETCH));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateOrderDetails(@Valid @RequestBody OrdersDto orderDto) {
        try {
            ResponseDto responseDto = iOrderService.updateOrderDetails(orderDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_UPDATE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_UPDATE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteOrderDetails(@RequestParam String orderId) {
        try {
            ResponseDto responseDto = iOrderService.deleteOrderDetails(orderId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        }
        catch (WebClientResponseException e) {
            // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto(OrdersConstants.STATUS_404, OrdersConstants.MESSAGE_404_DELETE));
            } else if (e.getStatusCode() == HttpStatus.EXPECTATION_FAILED) {
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new ResponseDto(OrdersConstants.STATUS_417, OrdersConstants.MESSAGE_417_DELETE));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_DELETE));
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_DELETE));
        }
    }

}
