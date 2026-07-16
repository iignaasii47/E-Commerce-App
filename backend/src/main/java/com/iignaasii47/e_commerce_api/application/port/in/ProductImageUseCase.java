package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.ImageData;

import java.util.Optional;

public interface ProductImageUseCase {

    Optional<ImageData> getImage(Long productId);
}
