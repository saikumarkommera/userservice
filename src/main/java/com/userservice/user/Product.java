package com.userservice.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;



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
