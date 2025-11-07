package com.example.backend.service;

import java.io.Serial;

public class SellerNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SellerNotFoundException(Long sellerId) {
        super("Seller " + sellerId + " not found");
    }
}
