package com.iignaasii47.e_commerce_api.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String category;
    private final String imageUrl;
    private final int stock;
    private final double rating;

    public Product(Long id, String name, String description, BigDecimal price,
                   String category, String imageUrl, int stock, double rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
