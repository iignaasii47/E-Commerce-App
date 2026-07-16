package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.ProductImageUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductImageRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductImageUseCaseService implements ProductImageUseCase {

    private final ProductImageRepository productImageRepository;

    public ProductImageUseCaseService(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @Override
    public Optional<ImageData> getImage(Long productId) {
        return productImageRepository.findByProductId(productId);
    }
}
