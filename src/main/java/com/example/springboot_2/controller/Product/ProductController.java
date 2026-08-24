package com.example.springboot_2.controller.Product;

import com.example.springboot_2.dto.*;
import com.example.springboot_2.model.Product.Product;
import com.example.springboot_2.repository.Product.ProductRepository;
import com.example.springboot_2.service.Product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    //API 1 + 2
    @GetMapping
    public Page<Product> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return productService.getProducts(page,size,sortBy,direction);
    }


    //API 3
    @GetMapping("/search")
    public Page<Product> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return productService.searchByName(keyword,page,size);
    }
    //API 4
    @GetMapping("/filter")
    public Page<Product> filter(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam int page,
            @RequestParam int size){
        return productService.filterByPriceRange(minPrice,maxPrice,page,size);
    }

    //API 5
    @GetMapping("/category/{categoryId}")
    public Page<Product> byCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return productService.getProductByCategory(categoryId,page,size,sortBy,direction);
    }

    //API 5 : @Query + Join Fetch
    @GetMapping("/category/joinfetch/{categoryId}")
    public Page<Product> byCategoryWithJoinFecth(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return productService.getProductByCategoryWithJoinFetch(categoryId,page,size,sortBy,direction);
    }

    //API 6: dùng Interface-based Projection
    @GetMapping("/summary/price-greater-than")
    public Page<Product> byPriceGreaterThan(
            @RequestParam BigDecimal price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return productService.getProductsByPriceGreaterThan(price,page,size);
    }

    //API 7
    @GetMapping("/summary/findAll")
    public Page<ProductSummary> getProductSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return productService.getProductSummaries(page,size,sortBy,direction);
    }

    //Luyện tập
    //API 7
    @GetMapping("/count/category/{categoryId}")
    public ResponseEntity<CountResponse> countByCategory(
            @PathVariable Long categoryId
    ){
        Long count = productService.countByCategory(categoryId);
        return ResponseEntity.ok(new CountResponse(categoryId,count));
    }

    //API 8
    @GetMapping("/category/{categoryId}/cheapest")
    public ResponseEntity<ProductSummary> getCheapestByCategory(@PathVariable Long categoryId){
        ProductSummary cheapest = productService.findCheapestByCategory(categoryId);
        if(cheapest == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cheapest);
    }

    //API 9
    @GetMapping("/category/{categoryId}/average")
    public ResponseEntity<AverageResponse> averagePrice(@PathVariable Long categoryId){
        BigDecimal averagePrice = productService.getAveragePriceByCategory(categoryId);
        if(averagePrice == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new AverageResponse(categoryId,averagePrice));
    }

    //API 11
    @GetMapping("/price/greater")
    public ResponseEntity<List<ProductDTO>> getPriceGreaterThan(@RequestParam BigDecimal priceX){
        List<ProductDTO> result = productService.getPriceGreaterThan(priceX);

        return ResponseEntity.ok(result);
    }
//    List<ProductDTO> getPriceGreaterThan(@RequestParam BigDecimal priceX){
//        return productService.getPriceGreaterThan(priceX);
//    }

    //API 13
    @PreAuthorize("hasRole(ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @Valid @RequestBody ProductRequest request){
        try {
            ProductResponse updated = productService.updateProduct(id, request);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException ex){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(ex.getMessage()));
        }
    }

    //API 15
    @PreAuthorize("hasRole(ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Đã xóa sản phẩm có id = " + id + " thành công"));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(ex.getMessage()));
        }
    }

    //API 16 findAll() viết lại bằng EntityGraph
    @GetMapping("/findAll")
    public ResponseEntity<List<Product>> findAll(){
        return ResponseEntity.ok(productService.findAll());
    }

    //Specification
    @GetMapping("/search-advanced")
    public Page<Product> searchAdvanced(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return productService.searchProducts(name,categoryId,minPrice,maxPrice,pageable);
    }

    //API 17 Thêm Product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    //API 17.5
    @PreAuthorize("hasRole(ADMIN)")
    @PostMapping("/post")
    public ApiResponse<Product> postProduct(@Valid @RequestBody ProductRequest request){
        return productService.postProduct(request);
    }
    //API 18 DÙng Optimistic Lock cập nhật stock (PATCH dùng để cập nhật một phần Khác với PUT cập nhật toàn bộ)
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id,
                                               @RequestParam int quantity){
        try{
            Product updated = productService.updateStock(id,quantity);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
