package com.example.springboot_2.service.Product;

import com.example.springboot_2.dto.CategorySummary;
import com.example.springboot_2.model.Product.Category;
import com.example.springboot_2.repository.Product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category addCategory(Category category){
        return categoryRepository.save(category);
    }

    //API 10
//    public List<CategorySummary> getCategorySummary(){
//        return categoryRepository.getCategorySummary();
//    }

    //API 11
     public List<CategorySummary> getCategoryMoreThanProducts(){
        return categoryRepository.getCategoryMoreThanProducts();
     }
}
