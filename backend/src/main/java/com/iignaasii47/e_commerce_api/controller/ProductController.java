package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.ProductResponse;
import com.iignaasii47.e_commerce_api.domain.model.Product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productUseCase.searchProducts(search);
        } else if (category != null && !category.isBlank()) {
            products = productUseCase.getProductsByCategory(category);
        } else {
            products = productUseCase.getAllProducts();
        }
        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        Product product = productUseCase.getProductById(id)
                .orElse(null);
        return product != null ? ProductResponse.from(product) : null;
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return productUseCase.getCategories();
    }

}
