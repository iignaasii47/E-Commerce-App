package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductRepository;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.ProductMapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpaProductRepository.findByCategory(category).stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> searchByName(String query) {
        return jpaProductRepository.searchByNameOrDescription(query).stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<String> findAllCategories() {
        return jpaProductRepository.findAllCategories();
    }

    @Override
    public void decrementStock(Long productId, int quantity) {
        jpaProductRepository.findById(productId).ifPresent(entity -> {
            entity.setStock(entity.getStock() - quantity);
            jpaProductRepository.save(entity);
        });
    }

}
