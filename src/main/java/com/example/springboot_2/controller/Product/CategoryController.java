package com.example.springboot_2.controller.Product;

import com.example.springboot_2.dto.CategorySummary;
import com.example.springboot_2.model.Product.Category;
import com.example.springboot_2.service.Product.CategoryService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@Getter
@Setter
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> addCategory(
            @Valid
            @RequestBody Category category
    ){
        categoryService.addCategory(category);
        return ResponseEntity.status(201).body(category);
    }

    //API 10
//    @GetMapping("/summary")
//    public ResponseEntity<List<CategorySummary>> getCategorySummary(){
//        List<CategorySummary> result = categoryService.getCategorySummary();
//        return ResponseEntity.ok(result);
//    }

    //API 11
    @GetMapping("summary/morethan1")
    public ResponseEntity<List<CategorySummary>> getCategoryMoreThan(){
        List<CategorySummary> result = categoryService.getCategoryMoreThanProducts();
        return ResponseEntity.ok(result);
    }

}
