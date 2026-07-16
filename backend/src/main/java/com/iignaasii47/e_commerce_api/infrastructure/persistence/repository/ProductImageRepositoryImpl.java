package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductImageRepository;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final JpaProductImageRepository jpaProductImageRepository;

    public ProductImageRepositoryImpl(JpaProductImageRepository jpaProductImageRepository) {
        this.jpaProductImageRepository = jpaProductImageRepository;
    }

    @Override
    public Optional<ImageData> findByProductId(Long productId) {
        return jpaProductImageRepository.findByProductId(productId)
                .map(entity -> new ImageData(entity.getData(), entity.getMimeType()));
    }
}
