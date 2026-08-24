package com.example.springboot_2.dto;

import com.example.springboot_2.exception.ErrorCode;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255,min = 6, message = "USERNAME_INVALID")
    private String name;

    @NotNull(message = "PRICE_INVALID")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "STOCK_INVALID")
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock;

    @NotNull(message = "CATEGORY_ID_INVALID")
    private Long categoryId;
}
