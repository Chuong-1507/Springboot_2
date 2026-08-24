package com.example.springboot_2.service.Product;

import com.example.springboot_2.dto.*;
import com.example.springboot_2.exception.AppException;
import com.example.springboot_2.exception.ErrorCode;
import com.example.springboot_2.mapper.ProductMapper;
import com.example.springboot_2.model.Product.Category;
import com.example.springboot_2.model.Product.Product;
import com.example.springboot_2.repository.Product.CategoryRepository;
import com.example.springboot_2.repository.Product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ser.jdk.JDKKeySerializers;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.List;

import static com.example.springboot_2.specification.ProductSpecification.*;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    ProductMapper productMapper;
    private static final int MAX_PAGE_SIZE = 100;

    //Method private: Tạo Pageable (phân trang) chung từ tham số + validate
    private Pageable buildPageable(int page,
                                   int size,
                                   String sortBy,
                                   String direction){
        //Ép page ko âm
        int safePage = Math.max(page,0);

        //Ép size trong khoảng hợp lệ (1 đén MAX_PAGE_SIZE)
        int safeSize = (size <= 0 || size > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : size;

        //Xác định hướng sắp xếp (ASC/DESC), mặc định là ASC nếu không truyền
        Sort.Direction sortDirection =
                Sort.Direction.fromString(
                (direction == null || direction.isBlank())
                        ? "asc"
                        :direction
        );

        //Tạo đối tượng Sort theo cột và hướng sắp xếp
        //Nếu không truyền sortBy thì không sắp xếp
        Sort sort = (sortBy == null || sortBy.isBlank())
                ? Sort.unsorted()
                : Sort.by(sortDirection, sortBy);

        //Tạo đối tượng Pageable gồm:
        //- Trang hiện tại
        //- Số bản ghi mỗi trang
        //- Thông tin sắp xếp theo
        return PageRequest.of(safePage,safeSize,sort);
    }

    //Method public: Các API
        //API 1 + 2 (basic + sort)
    public Page<Product> getProducts(int page,
                                     int size,
                                     String sortBy,
                                     String direction){
        Pageable pageable = buildPageable(page,size,sortBy,direction);
        return productRepository.findAll(pageable);
    }

    //API 3
    public Page<Product> searchByName(String keyword,
                                      int page,
                                      int size){
        Pageable pageable = buildPageable(page,size,null,null);
        return productRepository.findByNameContainingIgnoreCase(keyword,pageable);
    }

    //API 4
    public Page<Product> filterByPriceRange(BigDecimal minPrice,
                                            BigDecimal maxPrice,
                                            int page,
                                            int size){
        Pageable pageable = buildPageable(page,size,null,null);
        return productRepository.findByPriceBetween(minPrice,maxPrice,pageable);
    }

    //API 5
    public Page<Product> getProductByCategory(Long categoryId,
                                              int page,
                                              int size,
                                              String sortBy,
                                              String direction){
        Pageable pageable = buildPageable(page,size,sortBy,direction);
        return productRepository.findByCategoryId(categoryId,pageable);
    }

    //API 5 @Query + Join Fetch
    public Page<Product> getProductByCategoryWithJoinFetch(Long categoryId,
                                                           int page,
                                                           int size,
                                                           String sortBy,
                                                           String direction){
        Pageable pageable = buildPageable(page,size,sortBy,direction);
        return productRepository.findProductByCategoryIdWithJoinFetch(categoryId,pageable);
    }

    //API 6: Tìm Product giá tốt hơn
    public Page<Product> getProductsByPriceGreaterThan(BigDecimal price,
                                                              int page,
                                                              int size){
        Pageable pageable = buildPageable(page,size,null,null);
        return productRepository.findByPriceGreaterThan(price,pageable);
    }
    //API 7
    public Page<ProductSummary> getProductSummaries(int page,
                                                    int size,
                                                    String sortBy,
                                                    String direction){
        Pageable pageable = buildPageable(page,size,sortBy,direction);
        return productRepository.findAllProjectedBy(pageable);
    }
    //Luyện tập
    //API 7
    public Long countByCategory(Long categoryId){
        return productRepository.countProductsbyCategory(categoryId);
    }

    //API 8
    public ProductSummary findCheapestByCategory(Long categoryId){
        Pageable pageable = PageRequest.of(0,1);
        List<ProductSummary> result = productRepository.findCheapestByCategory(categoryId,pageable);
        return result.isEmpty() ? null : result.get(0);
    }

    //API 9
    public BigDecimal getAveragePriceByCategory(Long categoryId){
        return productRepository.getAveragePrice(categoryId);
    }

    //API 11
    public List<ProductDTO> getPriceGreaterThan(BigDecimal priceX){
        List<ProductDTO> result = productRepository.getPriceGreaterThan(priceX);
        if (result.isEmpty())
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        return result;
    }


    //API 13 Update thông tin product
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id)
                        .orElseThrow(()->new RuntimeException("Product not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(()->new RuntimeException("Category not found"));
//        product.setName(request.getName());
//        product.setPrice(request.getPrice());
//        product.setStock(request.getStock());
//        product.setCategory(category);
        productMapper.updateProduct(product,request,category);
        Product saved = productRepository.save(product);
        return toResponse(saved);

    }

    //API 15 Delete
    public void deleteById(Long id){
        if (!productRepository.existsById(id)){
            throw new RuntimeException("Product not found with id: "+ id);
        }
        productRepository.deleteById(id);
    }

    //API 16
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    //Specification
    public Page<Product> searchProducts(String name,
                                        Long categoryId,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        Pageable pageable){
        Specification<Product> spec = Specification
                .where(hasName(name))
                .and(hasCategoryId(categoryId))
                .and(priceGreaterThanOrEqual(minPrice))
                .and(priceLessThanOrEqual(maxPrice));
        return productRepository.findAll(spec,pageable);
    }

    //API 17 tạo một product
    public ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getName()
        );
    }

    private Product toEntity(ProductRequest request, Category category) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        return product;
    }
    
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + request.getCategoryId()));
        if (productRepository.existsByName(request.getName())){
            throw new RuntimeException("Đã tồn tại tên sản phẩm, vui lòng nhập tên khác");
        }
        Product product = productMapper.toProductWithCategory(request,category); // product.getId() == null
        Product saved = productRepository.save(product);// sau lưu xuống DB thì mới sinh id

        return toResponse(saved);
    }

    //API 17.5 tạo product theo cách khác
    public ApiResponse<Product> postProduct(ProductRequest request){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + request.getCategoryId()));
        Product product = toEntity(request,category); // product.getId() == null
        Product saved = productRepository.save(product);// sau lưu xuống DB thì mới sinh id

        ApiResponse<Product> apiResponse = new ApiResponse<>();
        apiResponse.setResult(saved);

        return apiResponse;
    }

    //API 18 DÙng Optimistic Lock để cập nhật stock
    public Product updateStock(Long id, int quantityChange){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));

        int newStock = product.getStock() + quantityChange;

        //Validate không cho stock âm
        if(newStock < 0){
            throw new RuntimeException("Số lượng tồn kho không đủ, hiện tại còn: "+product.getStock());
        }
        product.setStock(newStock);

        try{
            return productRepository.save(product);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Sản phẩm vừa được cập nhật bởi người khác, vui lòng thử lại");
        }
    }
}
