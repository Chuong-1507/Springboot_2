package com.example.springboot_2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountResponse {
    private Long categoryId;
    private Long productCount;

    public CountResponse(Long categoryId, Long productCount) {
        this.categoryId = categoryId;
        this.productCount = productCount;
    }

}
