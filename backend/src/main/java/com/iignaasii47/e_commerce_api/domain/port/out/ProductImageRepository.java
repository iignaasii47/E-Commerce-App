package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.ImageData;

import java.util.Optional;

public interface ProductImageRepository {

    Optional<ImageData> findByProductId(Long productId);
}
