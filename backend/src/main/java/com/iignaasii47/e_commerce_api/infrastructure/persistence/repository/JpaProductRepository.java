package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.ProductEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductRepository
        extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    List<ProductEntity> findByCategory(String category);

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) "
         + "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ProductEntity> searchByNameOrDescription(@Param("query") String query);

    @Query("SELECT DISTINCT p.category FROM ProductEntity p ORDER BY p.category")
    List<String> findAllCategories();

}
