package com.example.springboot_2.repository.Product;

import com.example.springboot_2.dto.CategorySummary;
import com.example.springboot_2.model.Product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    //API 10 Danh sách Category kèm số lượng sản phẩm (Group by + DTO Prọection)
//    @Query("SELECT new com.example.springboot_2.dto.CategorySummary(c.name, COUNT(p))" +
//            "FROM Category c JOIN c.products p GROUP BY c.name")
//    List<CategorySummary> getCategorySummary();

//    @Query("SELECT new com.example.springboot_2.dto.CategorySummary(p.category.name, COUNT(p)) " +
//            "FROM Product p " +
//            "GROUP BY p.category.name")
//    List<CategorySummary> getCategorySummary();

    //API 11 Lấy Category có nhiều hơn 1 sản phẩm
    @Query("SELECT new com.example.springboot_2.dto.CategorySummary(p.category.name,COUNT(p))" +
            "FROM Product p GROUP BY p.category.name HAVING COUNT(p) > 1")
    List<CategorySummary> getCategoryMoreThanProducts();
}

