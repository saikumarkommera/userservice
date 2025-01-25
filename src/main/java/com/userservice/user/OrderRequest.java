package com.userservice.user;

import org.springframework.web.bind.annotation.RequestParam;

public record OrderRequest(long userId ,long productId ,int quantity ) {
}
