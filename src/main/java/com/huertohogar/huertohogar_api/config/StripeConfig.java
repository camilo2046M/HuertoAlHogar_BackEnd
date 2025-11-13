package com.huertohogar.huertohogar_api.config; // (Use your package name)

import com.stripe.Stripe; // Import the Stripe class
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    // 1. Read the secret key from your properties file
    @Value("${stripe.secret.key}")
    private String secretKey;

    // 2. This runs after the app starts
    @PostConstruct
    public void init() {
        // 3. Set the API key globally for the Stripe SDK
        Stripe.apiKey = secretKey;
        System.out.println("Stripe SDK initialized successfully.");
    }
}