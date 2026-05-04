package com.example.backend_tmdt.controller;

import com.example.backend_tmdt.dto.request.CreateProductRequest;
import com.example.backend_tmdt.dto.request.SearchProductRequest;
import com.example.backend_tmdt.dto.request.UpdateProductRequest;
import com.example.backend_tmdt.dto.response.ProductListResponse;
import com.example.backend_tmdt.dto.response.ProductResponse;
import com.example.backend_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // seller thêm sản phẩm mới
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // seller cập nhật thông tin sản phẩm (tên, descrip, giá, tồn kho, ẩn/hiện, danh mục...)
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    // seller xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // buyer/guest xem danh sách sản phẩm khi mới vào
    @GetMapping
    public ResponseEntity<ProductListResponse> getVisibleProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        ProductListResponse response = productService.getVisibleProducts(page, pageSize);
        return ResponseEntity.ok(response);
    }

    // Tìm kiếm sản phẩm theo filter
    @PostMapping("/search")
    public ResponseEntity<ProductListResponse> searchProducts(@RequestBody SearchProductRequest request) {
        ProductListResponse response = productService.searchProducts(request);
        return ResponseEntity.ok(response);
    }

    // xem chi tiết thông tin 1 sản phẩm
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductDetails(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetails(id);
        return ResponseEntity.ok(response);
    }

    // Xem thông tin của shop, gồm cả sản phẩm
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<ProductListResponse> getProductsByShop(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        ProductListResponse response = productService.getProductsByShop(shopId, page, pageSize);
        return ResponseEntity.ok(response);
    }

    // Lấy sản phẩm theo category (bao gồm category con)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ProductListResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        ProductListResponse response = productService.getProductsByCategory(categoryId, page, pageSize);
        return ResponseEntity.ok(response);
    }
}
