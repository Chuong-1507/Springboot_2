package com.example.springboot_2.dto;

import lombok.Getter;
import lombok.Setter;

import javax.swing.plaf.basic.BasicIconFactory;
import java.math.BigDecimal;

@Setter
@Getter
public class AverageResponse {
    private Long categoryId;
    private BigDecimal average;

    public AverageResponse(Long categoryId, BigDecimal average) {
        this.categoryId = categoryId;
        this.average = average;
    }
}
