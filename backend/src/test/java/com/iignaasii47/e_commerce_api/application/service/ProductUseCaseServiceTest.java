package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCaseService productUseCaseService;

    private final Product product = new Product(1L, "Keyboard", "desc",
            new BigDecimal("149.99"), "peripherals", "img", 10, 4.5);

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productUseCaseService.getAllProducts();

        assertThat(result).hasSize(1).contains(product);
    }

    @Test
    void shouldReturnProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productUseCaseService.getProductById(1L);

        assertThat(result).contains(product);
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productUseCaseService.getProductById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnProductsByCategory() {
        when(productRepository.findByCategory("peripherals")).thenReturn(List.of(product));

        List<Product> result = productUseCaseService.getProductsByCategory("peripherals");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldSearchProducts() {
        when(productRepository.searchByName("keyboard")).thenReturn(List.of(product));

        List<Product> result = productUseCaseService.searchProducts("keyboard");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnCategories() {
        when(productRepository.findAllCategories()).thenReturn(List.of("peripherals", "audio"));

        List<String> result = productUseCaseService.getCategories();

        assertThat(result).containsExactly("peripherals", "audio");
    }

}
