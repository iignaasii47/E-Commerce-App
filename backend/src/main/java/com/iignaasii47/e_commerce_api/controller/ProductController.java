package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ProductImageUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.controller.dto.PaginatedProductResponse;
import com.iignaasii47.e_commerce_api.controller.dto.ProductResponse;
import com.iignaasii47.e_commerce_api.domain.exception.ProductNotFoundException;
import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.model.PageResult;
import com.iignaasii47.e_commerce_api.domain.model.Product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Browse and search products in the store catalog")
public class ProductController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "price");
    private static final Set<String> ALLOWED_SORT_DIRECTIONS = Set.of("asc", "desc");
    private static final int MAX_PAGE_SIZE = 50;

    private final ProductUseCase productUseCase;
    private final ProductImageUseCase productImageUseCase;

    public ProductController(ProductUseCase productUseCase, ProductImageUseCase productImageUseCase) {
        this.productUseCase = productUseCase;
        this.productImageUseCase = productImageUseCase;
    }

    @GetMapping
    @Operation(summary = "List or search products",
            description = "Returns paginated products, optionally filtered by category or search query, with sorting.")
    @ApiResponse(responseCode = "200", description = "Products returned successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameters", content = @Content)
    public ResponseEntity<Object> getAllProducts(
            @Parameter(description = "Filter products by category slug", example = "peripherals")
            @RequestParam(required = false) String category,
            @Parameter(description = "Full-text search across product name and description", example = "keyboard")
            @RequestParam(required = false) String search,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (max 50)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field and direction (e.g., price,asc or name,desc)", example = "price,asc")
            @RequestParam(required = false) String sort) {

        if (page < 0) {
            return ResponseEntity.badRequest().body("Page index must not be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest()
                    .body("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        String sortBy = "name";
        String sortDir = "asc";
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length != 2) {
                return ResponseEntity.badRequest()
                        .body("Sort parameter must be in format 'field,direction' (e.g., price,asc)");
            }
            sortBy = parts[0].trim();
            sortDir = parts[1].trim().toLowerCase();
            if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
                return ResponseEntity.badRequest()
                        .body("Invalid sort field: " + sortBy + ". Allowed: " + ALLOWED_SORT_FIELDS);
            }
            if (!ALLOWED_SORT_DIRECTIONS.contains(sortDir)) {
                return ResponseEntity.badRequest()
                        .body("Invalid sort direction: " + sortDir + ". Allowed: " + ALLOWED_SORT_DIRECTIONS);
            }
        }

        PageResult<Product> pageResult = productUseCase.getProducts(
                search, category, page, size, sortBy, sortDir);

        List<ProductResponse> content = pageResult.getContent().stream()
                .map(ProductResponse::from)
                .toList();

        PaginatedProductResponse response = new PaginatedProductResponse(
                content,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getCurrentPage(),
                pageResult.getPageSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Returns detailed information about a single product.")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    public ProductResponse getProductById(
            @Parameter(description = "Product identifier", example = "1")
            @PathVariable Long id) {
        Product product = productUseCase.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return ProductResponse.from(product);
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
    public ResponseEntity<Object> getImage(
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
