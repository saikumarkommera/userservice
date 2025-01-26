package com.userservice.user;

public record OrderRequest(long userId ,long productId ,int quantity ) {
}
