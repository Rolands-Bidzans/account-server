package com.eazybytes.accounts.constants;

public final class OrdersConstants {

    private OrdersConstants() {
        // restrict instantiation
    }

    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "Order created successfully";

    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 = "Request processed successfully";
    public static final String MESSAGE_200_FETCH_ALL = "All orders retrieved successfully";
    public static final String MESSAGE_200_DELETE = "Order deleted successfully";
    public static final String MESSAGE_200_UPDATE = "Order updated successfully";

    public static final String STATUS_400 = "400";
    public static final String MESSAGE_400_CREATE = "Order placement failed: Invalid order details.";
    public static final String MESSAGE_400_UPDATE = "Invalid update request. Please check the order details.";

    public static final String STATUS_404 = "404";
    public static final String MESSAGE_404_FETCH = "Order not found for the given ID.";
    public static final String MESSAGE_404_FETCH_ALL = "No orders found for the given criteria.";
    public static final String MESSAGE_404_UPDATE = "Order to update not found.";
    public static final String MESSAGE_404_DELETE = "Order to delete not found.";

    public static final String STATUS_409 = "409";
    public static final String MESSAGE_409_UPDATE = "Update conflict: Order cannot be modified in current state.";

    public static final String STATUS_417 = "417";
    public static final String MESSAGE_417_UPDATE = "Update operation failed. Please try again or contact the Dev team.";
    public static final String MESSAGE_417_DELETE = "Delete operation failed. Please try again or contact the Dev team.";

    public static final String STATUS_500 = "500";
    public static final String  MESSAGE_500_DELETE= "Delete operation failed. Please try again or contact Dev team";
    public static final String  MESSAGE_500_FETCH= "Fetch operation failed. Please try again or contact Dev team";
    public static final String MESSAGE_500_FETCH_ALL = "Fetch All operation failed. Please try again or contact Dev team";
    public static final String  MESSAGE_500_UPDATE= "Update operation failed. Please try again or contact Dev team";
    public static final String MESSAGE_500_CREATE = "Order could not be placed due to an internal error.";


}
