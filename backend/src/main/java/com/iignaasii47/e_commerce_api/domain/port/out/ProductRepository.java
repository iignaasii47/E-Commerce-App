package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.PageResult;
import com.iignaasii47.e_commerce_api.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    List<Product> findByCategory(String category);

    List<Product> searchByName(String query);

    List<String> findAllCategories();

    void decrementStock(Long productId, int quantity);

    PageResult<Product> findProducts(String search, String category, int page, int size,
                                     String sortBy, String sortDir);

}
