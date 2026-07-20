package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.port.out.CartRepository;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper.CartItemMapper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartRepositoryImpl implements CartRepository {

    private final JpaCartItemRepository jpaCartItemRepository;

    public CartRepositoryImpl(JpaCartItemRepository jpaCartItemRepository) {
        this.jpaCartItemRepository = jpaCartItemRepository;
    }

    @Override
    public List<CartItem> findByUserId(Long userId) {
        return jpaCartItemRepository.findByUserId(userId).stream()
                .map(CartItemMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public CartItem addItem(Long userId, Long productId, String productName, BigDecimal unitPrice, int quantity) {
        CartItemEntity entity = new CartItemEntity(null, userId, productId, productName, unitPrice, quantity);
        CartItemEntity saved = jpaCartItemRepository.save(entity);
        return CartItemMapper.toDomain(saved);
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        jpaCartItemRepository.deleteByUserIdAndId(userId, cartItemId);
    }

    @Override
    public CartItem findByUserAndProduct(Long userId, Long productId) {
        return jpaCartItemRepository.findByUserIdAndProductId(userId, productId)
                .map(CartItemMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateQuantity(Long cartItemId, int quantity) {
        jpaCartItemRepository.findById(cartItemId).ifPresent(entity -> {
            entity.setQuantity(quantity);
            jpaCartItemRepository.save(entity);
        });
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        jpaCartItemRepository.deleteByUserId(userId);
    }

}
