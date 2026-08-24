package com.example.springboot_2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CategorySummary {
    private String categoryName;
    private Long ProductCount;
}
