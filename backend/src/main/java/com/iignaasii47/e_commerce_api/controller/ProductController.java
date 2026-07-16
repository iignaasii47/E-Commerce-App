package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ProductImageUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.ProductResponse;
import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.model.Product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Browse and search products in the store catalog")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductImageUseCase productImageUseCase;

    public ProductController(ProductUseCase productUseCase, ProductImageUseCase productImageUseCase) {
        this.productUseCase = productUseCase;
        this.productImageUseCase = productImageUseCase;
    }

    @GetMapping
    @Operation(summary = "List or search products",
            description = "Returns all products, optionally filtered by category or search query.")
    @ApiResponse(responseCode = "200", description = "Products returned successfully")
    public List<ProductResponse> getAllProducts(
            @Parameter(description = "Filter products by category slug", example = "peripherals")
            @RequestParam(required = false) String category,
            @Parameter(description = "Full-text search across product name and description", example = "keyboard")
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
    @Operation(summary = "Get product by ID",
            description = "Returns detailed information about a single product.")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "204", description = "Product not found", content = @Content)
    public ProductResponse getProductById(
            @Parameter(description = "Product identifier", example = "1")
            @PathVariable Long id) {
        Product product = productUseCase.getProductById(id)
                .orElse(null);
        return product != null ? ProductResponse.from(product) : null;
    }

    @GetMapping("/categories")
    @Operation(summary = "List all product categories",
            description = "Returns a deduplicated list of all available product category slugs.")
    @ApiResponse(responseCode = "200", description = "Categories returned successfully")
    public List<String> getCategories() {
        return productUseCase.getCategories();
    }

    @GetMapping("/{id}/image")
    @Operation(summary = "Get product image",
            description = "Returns the product image binary data, or redirects to an external image URL.")
    @ApiResponse(responseCode = "200", description = "Image binary returned")
    @ApiResponse(responseCode = "302", description = "Redirect to external image URL")
    @ApiResponse(responseCode = "404", description = "No image available", content = @Content)
    public ResponseEntity<?> getImage(
            @Parameter(description = "Product identifier", example = "1")
            @PathVariable Long id) {
        Optional<ImageData> imageData = productImageUseCase.getImage(id);
        if (imageData.isPresent()) {
            ImageData img = imageData.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(img.getMimeType()))
                    .body(img.getData());
        }

        Product product = productUseCase.getProductById(id).orElse(null);
        if (product != null && product.getImageUrl() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, product.getImageUrl())
                    .build();
        }

        return ResponseEntity.notFound().build();
    }
}
