package com.example.springboot_2.dto;

import java.math.BigDecimal;

public interface ProductSummary {
    String getName();
    BigDecimal getPrice();

    CategoryInfo getCategory();

    interface CategoryInfo{
        Long getId();
        String getName();
    }
}
