package com.example.backend.service;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(Long sellerId) {
        super("Seller " + sellerId + " not found");
    }
}
