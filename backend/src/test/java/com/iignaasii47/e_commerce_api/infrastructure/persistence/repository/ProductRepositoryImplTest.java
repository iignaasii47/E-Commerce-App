package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.ProductMapper;

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
class ProductRepositoryImplTest {

    @Mock
    private JpaProductRepository jpaProductRepository;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    private final ProductEntity entity = new ProductEntity(1L, "Keyboard", "desc",
            new BigDecimal("149.99"), "peripherals", "img", 10, 4.5);

    @Test
    void shouldFindAll() {
        when(jpaProductRepository.findAll()).thenReturn(List.of(entity));

        List<Product> result = productRepository.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Keyboard");
    }

    @Test
    void shouldFindById() {
        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Product> result = productRepository.findById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindByCategory() {
        when(jpaProductRepository.findByCategory("peripherals")).thenReturn(List.of(entity));

        List<Product> result = productRepository.findByCategory("peripherals");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldSearchByName() {
        when(jpaProductRepository.searchByNameOrDescription("keyboard")).thenReturn(List.of(entity));

        List<Product> result = productRepository.searchByName("keyboard");

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindAllCategories() {
        when(jpaProductRepository.findAllCategories()).thenReturn(List.of("peripherals", "audio"));

        List<String> result = productRepository.findAllCategories();

        assertThat(result).containsExactly("peripherals", "audio");
    }

}
