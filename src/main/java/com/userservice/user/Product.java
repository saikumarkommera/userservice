package com.userservice.user;

public record Product (
        Long id,
        String title,
        String description,
        String category,
        double price,
        double rating,
        int stock,
        String message
){}
