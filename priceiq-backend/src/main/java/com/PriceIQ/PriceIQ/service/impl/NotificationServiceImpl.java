package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService { //! NOT COMPLETED

    @Override
    public void sendOrderConfirmation(Long userId, Long orderId) {
        log.info(
                "Sending order confirmation to user {} for order {}",
                userId,
                orderId
        );
    }

    @Override
    public void sendPaymentSuccess(Long userId, Long paymentId) {
        log.info(
                "Sending payment success notification to user {} for payment {}",
                userId,
                paymentId
        );
    }

    @Override
    public void sendPriceDropAlert(Long userId, Long productId) {
        log.info(
                "Sending price drop alert to user {} for product {}",
                userId,
                productId
        );
    }

    @Override
    public void sendWishlistBackInStock(Long userId, Long productId) {
        log.info(
                "Sending back in stock notification to user {} for product {}",
                userId,
                productId
        );
    }
}