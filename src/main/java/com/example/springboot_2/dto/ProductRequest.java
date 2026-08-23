package com.example.springboot_2.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class ProductCreateRequest {
    private String name;
    private BigDecimal price;
    private Long categoryId;
}
