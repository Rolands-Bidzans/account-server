package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.OrdersConstants;
import com.eazybytes.accounts.dto.NotificationEvent;
import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.publisher.NotificationProducer;
import com.eazybytes.accounts.service.IOrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    WebClient webClient;

    private NotificationProducer notificationProducer;

    @Override
    public ResponseDto createOrder(String receiverEmail, OrdersDto orderDto) {

        orderDto.setOrderId(UUID.randomUUID().toString());
        String uri = "http://localhost:8083/api/orders/create";

        try {
            webClient.post().uri(uri)
                    .bodyValue(orderDto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            NotificationEvent event = new NotificationEvent();
            event.setReceiverEmail(receiverEmail);
            event.setMessage("Order status is updated to PLACED state");
            event.setOrder(orderDto);

            notificationProducer.sendMessage(event);

            return new ResponseDto(OrdersConstants.STATUS_201, OrdersConstants.MESSAGE_201);
        }
        catch (WebClientResponseException e) { // Handle known exceptions
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseDto(OrdersConstants.STATUS_400, OrdersConstants.MESSAGE_400_CREATE);
            }
            return new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE);
        }
        catch (Exception e) {  // Catch any other unexpected errors
            return new ResponseDto(OrdersConstants.STATUS_500, OrdersConstants.MESSAGE_500_CREATE);
        }
    }

    @Override
    public Set<OrdersDto> fetchAllOrders(String accountNumber) {

        String uri = "http://localhost:8083/api/orders/fetchAll?accountNumber=" + accountNumber;

        // Proper WebClient call for GET request returning a Set<OrdersDto>
        Set<OrdersDto> ordersDto = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Set<OrdersDto>>() {})
                .block()
                .getBody();

        return ordersDto;
    }

    @Override
    public OrdersDto fetchOrder(String orderId) {
        String uri = "http://localhost:8083/api/orders/fetch?orderId=" + orderId;

        OrdersDto ordersDto = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(OrdersDto.class)
                .block()
                .getBody();

        return ordersDto;
    }

    @Override
    public ResponseDto updateOrderDetails(OrdersDto orderDto) {
        String uri = "http://localhost:8083/api/orders/update";

        ResponseDto responseDto = webClient.put()
                .uri(uri)
                .bodyValue(orderDto)
                .retrieve()
                .toEntity(ResponseDto.class)
                .block()
                .getBody();

        return responseDto;
    }

    @Override
    public ResponseDto deleteOrderDetails(String orderId) {
        String uri = "http://localhost:8083/api/orders/delete?orderId=" + orderId;

        ResponseDto responseDto = webClient.delete()
                .uri(uri)
                .retrieve()
                .toEntity(ResponseDto.class)  // Expecting a ResponseDto as response
                .block()
                .getBody();

        return responseDto;
    }
}
