package com.example.springboot_2.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
}
