package com.PriceIQ.PriceIQ.service;

public interface NotificationService {

    void sendOrderConfirmation(Long userId, Long orderId);

    void sendPaymentSuccess(Long userId, Long paymentId);

    void sendPriceDropAlert(Long userId, Long productId);

    void sendWishlistBackInStock(Long userId, Long productId);
}