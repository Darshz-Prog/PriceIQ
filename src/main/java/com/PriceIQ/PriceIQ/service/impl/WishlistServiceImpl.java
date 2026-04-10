package com.PriceIQ.PriceIQ.service.impl;

import com.PriceIQ.PriceIQ.dto.request.WishlistRequest;
import com.PriceIQ.PriceIQ.dto.response.WishlistResponse;
import com.PriceIQ.PriceIQ.entity.Product;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.entity.Wishlist;
import com.PriceIQ.PriceIQ.exception.ResourceNotFoundException;
import com.PriceIQ.PriceIQ.repository.ProductRepository;
import com.PriceIQ.PriceIQ.repository.UserRepository;
import com.PriceIQ.PriceIQ.repository.WishlistRepository;
import com.PriceIQ.PriceIQ.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishlistResponse> getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WishlistResponse addToWishlist(WishlistRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
             Wishlist existing = wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
             return mapToResponse(existing);
        }

        Wishlist newWishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        Wishlist savedWishlist = wishlistRepository.save(newWishlist);

        return mapToResponse(savedWishlist);
    }

    @Override
    public WishlistResponse removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        wishlistRepository.delete(wishlist);
        return mapToResponse(wishlist);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        return WishlistResponse.builder()
                .wishlistId(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .productImage(wishlist.getProduct().getImages() != null && !wishlist.getProduct().getImages().isEmpty() ? wishlist.getProduct().getImages().get(0).getImageUrl() : null)
                .brand(wishlist.getProduct().getBrand())
                .category(wishlist.getProduct().getCategory())
                .build();
    }
}