package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.PageResult;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductRepository;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.ProductMapper;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.specification.ProductSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public PageResult<Product> findProducts(String search, String category, int page, int size,
                                             String sortBy, String sortDir) {
        Specification<ProductEntity> spec = (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProductSpecification.matchesSearch(search));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and(ProductSpecification.hasCategory(category));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ProductEntity> result = jpaProductRepository.findAll(spec, pageRequest);

        List<Product> products = result.getContent().stream()
                .map(ProductMapper::toDomain)
                .toList();

        return new PageResult<>(
                products,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

}
