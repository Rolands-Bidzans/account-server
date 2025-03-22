package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.OrdersDto;
import com.eazybytes.accounts.dto.ResponseDto;

import java.util.Set;

public interface IOrderService {
    ResponseDto createOrder(String receiverEmail, OrdersDto orderDto);
    Set<OrdersDto> fetchAllOrders(String accountNumber);
    OrdersDto fetchOrder(String orderId);
    ResponseDto updateOrderDetails(OrdersDto orderDto);
    ResponseDto deleteOrderDetails(String orderId);

}
