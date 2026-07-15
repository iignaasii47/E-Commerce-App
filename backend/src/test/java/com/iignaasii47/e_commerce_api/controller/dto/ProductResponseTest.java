package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseTest {

    @Test
    void shouldMapProductToResponse() {
        Product product = TestFixtures.aKeyboardProduct();

        ProductResponse response = ProductResponse.from(product);

        assertThat(response).extracting(ProductResponse::getId, ProductResponse::getName,
                        ProductResponse::getDescription, ProductResponse::getPrice,
                        ProductResponse::getCategory, ProductResponse::getImage,
                        ProductResponse::getStock, ProductResponse::getRating)
                .containsExactly(product.getId(), product.getName(), product.getDescription(),
                        product.getPrice(), product.getCategory(), product.getImageUrl(),
                        product.getStock(), product.getRating());
    }

    @Test
    void shouldMapImageUrlToImageField() {
        Product product = Product.builder()
                .id(1L).name("Test").description("desc")
                .price(BigDecimal.ONE).category("cat").imageUrl("https://example.com/img.png")
                .stock(0).rating(0).build();

        ProductResponse response = ProductResponse.from(product);

        assertThat(response.getImage()).isEqualTo("https://example.com/img.png");
    }

}
