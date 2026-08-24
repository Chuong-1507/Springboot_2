package com.example.springboot_2.repository.Product;

import com.example.springboot_2.dto.ProductDTO;
import com.example.springboot_2.model.Product.Product;
import com.example.springboot_2.dto.ProductSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {
    // API 3: tìm theo tên, không phân biệt hoa thường
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    // API 4: tìm theo khoảng giá
    Page<Product> findByPriceBetween(BigDecimal minPrice,BigDecimal maxPrice, Pageable pageable);

    // API 5: tìm theo category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // API 5: tìm theo category bằng @Query + JOIN FETCH
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.category.id = :categoryId")
    Page<Product> findProductByCategoryIdWithJoinFetch(
            @Param("categoryId") Long categoryId,Pageable pageable
    );

    //API 6: Tìm Product giá tốt hơn
    @EntityGraph(value = "Product.withCategory")
    Page<Product> findByPriceGreaterThan(BigDecimal price, Pageable pageable);

    //API 7 : gọi toàn bộ Name và Price của Product
    Page<ProductSummary> findAllProjectedBy(Pageable pageable);

    //Luyện tập
   //API 7 - Đếm số sản phầm theo Catgory
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsbyCategory(@Param("categoryId") Long categoryId);

    //API 8 Lấy sản phẩm rẻ nhất trong 1 Category
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.price ASC")
    List<ProductSummary> findCheapestByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    //API 9 Thống kê giá trung bình theo Category
    @Query("SELECT avg(p.price) FROM Product p WHERE p.category.id = :categoryId")
    BigDecimal getAveragePrice(@Param("categoryId")Long categoryId);

    //API 11 Tìm tất cả Product có giá > X (truyền param)
    @Query("SELECT new com.example.springboot_2.dto.ProductDTO(p.id,p.name,p.price,c.name)" +
            " FROM Product p JOIN p.category c " +
            "WHERE p.price > :priceX")
    List<ProductDTO> getPriceGreaterThan(@Param("priceX") BigDecimal priceX);


    //API 16 findAll() viết lại bằng EntityGraph
    @EntityGraph(attributePaths = "category")
    public List<Product> findAll();


    boolean existsByName(String name);
}
