package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductUseCase {

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getProductsByCategory(String category);

    List<Product> searchProducts(String query);

    List<String> getCategories();

}
