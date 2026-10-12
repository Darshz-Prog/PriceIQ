
package com.PriceIQ.PriceIQ.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordCheckExample {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // Example: storing a password
        String rawPassword = "useruser";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Encoded password: " + encodedPassword);

        // Verifying the password from your DB
        String dbHash = "$2a$10$7l/QminCZivUyFrZvL7lHeToM5J3Q8M6Ttpmu7EUSpiIX.vbPQ/Lu";
        boolean matches = encoder.matches("useruser", dbHash);
        System.out.println("Password from DB matches 'useruser': " + matches);
    }
}
