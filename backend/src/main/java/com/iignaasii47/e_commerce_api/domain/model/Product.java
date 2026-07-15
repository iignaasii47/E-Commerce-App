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

    Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.stock = builder.stock;
        this.rating = builder.rating;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public int getStock() { return stock; }
    public double getRating() { return rating; }

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

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private String imageUrl;
        private int stock;
        private double rating;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder stock(int stock) { this.stock = stock; return this; }
        public Builder rating(double rating) { this.rating = rating; return this; }
        public Product build() { return new Product(this); }
    }
}
