package com.example.springboot_2.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(404,"Product not found"),
    USERNAME_INVALID(1000,"USERNAME MUST BE AT LEAST 6 CHARACTERS AND NO MORE THAN 255 CHARACTERS"),
    PRICE_INVALID(1001,"PRICE MUST BE NOT NULL"),
    STOCK_INVALID(1002,"STOCK MUST BE NOT NULL"),
    CATEGORY_ID_INVALID(1003,"CATEGORY ID MUST BE NOT NULL"),
    USER_NOT_FOUND(1111,"USER NOT FOUND");
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
