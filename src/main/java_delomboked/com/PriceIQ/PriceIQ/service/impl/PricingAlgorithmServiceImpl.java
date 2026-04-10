package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.PricingWeightsRequest;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.service.PricingAlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PricingAlgorithmServiceImpl implements PricingAlgorithmService {

    private final ProductRepository productRepository;

    @Override
    public Double getDynamicPrice(Long productId, Long userId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        double basePrice = product.getPrice();
        double stockFactor = product.getStockQuantity() < 10 ? 1.15 : 1.00;
        double demandFactor = 1.10;

        return basePrice * stockFactor * demandFactor;
    }

    @Override
    public void updatePricingWeights(PricingWeightsRequest request) {
        System.out.println("Updated pricing weights");
    }
}