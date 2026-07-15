package com.iignaasii47.e_commerce_api.infrastructure.persistence.repository;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartRepositoryImplTest {

    @Mock
    private JpaCartItemRepository jpaCartItemRepository;

    @InjectMocks
    private CartRepositoryImpl cartRepository;

    private final CartItemEntity entity = new CartItemEntity(1L, 10L, 5L, "Keyboard",
            new BigDecimal("149.99"), 2);

    private final CartItemEntity savedEntity = new CartItemEntity(2L, 10L, 5L, "Keyboard",
            new BigDecimal("149.99"), 2);

    @Test
    void shouldFindByUserId() {
        when(jpaCartItemRepository.findByUserId(10L)).thenReturn(List.of(entity));

        List<CartItem> result = cartRepository.findByUserId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Keyboard");
        assertThat(result.get(0).getUserId()).isEqualTo(10L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoItems() {
        when(jpaCartItemRepository.findByUserId(99L)).thenReturn(List.of());

        List<CartItem> result = cartRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldAddItem() {
        when(jpaCartItemRepository.save(any(CartItemEntity.class))).thenReturn(savedEntity);

        CartItem result = cartRepository.addItem(10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getProductName()).isEqualTo("Keyboard");
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnitPrice()).isEqualByComparingTo("149.99");
    }

    @Test
    void shouldRemoveItem() {
        cartRepository.removeItem(5L);

        verify(jpaCartItemRepository).deleteById(5L);
    }

    @Test
    void shouldFindByUserAndProduct() {
        when(jpaCartItemRepository.findByUserIdAndProductId(10L, 5L)).thenReturn(Optional.of(entity));

        CartItem result = cartRepository.findByUserAndProduct(10L, 5L);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(5L);
    }

    @Test
    void shouldReturnNullWhenUserAndProductNotFound() {
        when(jpaCartItemRepository.findByUserIdAndProductId(10L, 99L)).thenReturn(Optional.empty());

        CartItem result = cartRepository.findByUserAndProduct(10L, 99L);

        assertThat(result).isNull();
    }

    @Test
    void shouldUpdateQuantity() {
        when(jpaCartItemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(jpaCartItemRepository.save(entity)).thenReturn(entity);

        cartRepository.updateQuantity(1L, 10);

        assertThat(entity.getQuantity()).isEqualTo(10);
        verify(jpaCartItemRepository).save(entity);
    }

    @Test
    void shouldNotUpdateQuantityWhenItemNotFound() {
        when(jpaCartItemRepository.findById(99L)).thenReturn(Optional.empty());

        cartRepository.updateQuantity(99L, 5);

        verify(jpaCartItemRepository).findById(99L);
    }

    @Test
    void shouldClearCart() {
        cartRepository.clearCart(10L);

        verify(jpaCartItemRepository).deleteByUserId(10L);
    }
}
