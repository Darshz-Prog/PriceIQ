package com.PriceIQ.PriceIQ.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Long reviewId;

    private Long userId;

    private String userName;

    private Integer rating;

    private String comment;

    private Boolean verifiedPurchase;

    private LocalDateTime createdAt;
}