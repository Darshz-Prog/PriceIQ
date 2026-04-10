package com.PriceIQ.PriceIQ.dto.response;

import com.PriceIQ.PriceIQ.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private String token;
}