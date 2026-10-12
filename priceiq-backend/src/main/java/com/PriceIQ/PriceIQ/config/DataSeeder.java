package com.PriceIQ.PriceIQ.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.PriceIQ.PriceIQ.entity.Role;
import com.PriceIQ.PriceIQ.entity.User;
import com.PriceIQ.PriceIQ.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {

            if (!userRepository.existsByEmail("admin@gmail.com")) {

                User admin = new User();

                admin.setFirstName("admin");
                admin.setLastName("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setPhoneNumber("9999999999");
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }
        };
    }
}