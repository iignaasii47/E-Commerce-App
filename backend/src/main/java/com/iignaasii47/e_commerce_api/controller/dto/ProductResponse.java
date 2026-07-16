package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Product listing information")
public class ProductResponse {

    @Schema(description = "Unique product identifier", example = "1")
    private Long id;
    @Schema(description = "Product name", example = "Mechanical Keyboard MK-750")
    private String name;
    @Schema(description = "Full product description", example = "Premium mechanical keyboard with Cherry MX Blue switches")
    private String description;
    @Schema(description = "Price in USD", example = "149.99")
    private BigDecimal price;
    @Schema(description = "Product category slug", example = "peripherals")
    private String category;
    @Schema(description = "Product image URL or local endpoint", example = "https://placehold.co/400x400?text=MK-750")
    private String image;
    @Schema(description = "Available stock count", example = "23")
    private int stock;
    @Schema(description = "Average rating from 0.0 to 5.0", example = "4.7")
    private double rating;

    public ProductResponse() {
        // Required for JSON deserialization
    }

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.getId();
        response.name = product.getName();
        response.description = product.getDescription();
        response.price = product.getPrice();
        response.category = product.getCategory();
        response.image = product.getImageUrl();
        response.stock = product.getStock();
        response.rating = product.getRating();
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}
