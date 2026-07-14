package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByUserId(Long userId);

    Optional<CartItemEntity> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);

}
