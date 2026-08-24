package com.example.springboot_2.mapper;

import com.example.springboot_2.dto.ProductRequest;
import com.example.springboot_2.dto.ProductResponse;
import com.example.springboot_2.model.Product.Category;
import com.example.springboot_2.model.Product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest request);
    ProductResponse toProductResponse(ProductRequest request);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "category", source = "category")
    Product toProductWithCategory(ProductRequest request, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "category", source = "category")
    void updateProduct(@MappingTarget Product product, ProductRequest request,Category category);

}
