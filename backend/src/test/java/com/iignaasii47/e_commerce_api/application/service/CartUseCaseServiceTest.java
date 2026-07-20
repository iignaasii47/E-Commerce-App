package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.CartRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.SecurityContextProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartUseCaseServiceTest {

    private static final Long USER_ID = 10L;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private SecurityContextProvider securityContextProvider;

    @InjectMocks
    private CartUseCaseService cartUseCaseService;

    @Test
    void shouldGetCart() {
        CartItem item = new CartItem(1L, USER_ID, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(List.of(item));

        List<CartItem> result = cartUseCaseService.getCart();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Keyboard");
    }

    @Test
    void shouldReturnEmptyCart() {
        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(List.of());

        List<CartItem> result = cartUseCaseService.getCart();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldAddNewItemToCart() {
        Product product = Product.builder()
                .id(5L).name("Keyboard").price(new BigDecimal("149.99")).build();
        CartItem expectedItem = new CartItem(1L, USER_ID, 5L, "Keyboard", new BigDecimal("149.99"), 3);

        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(productUseCase.getProductById(5L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProduct(USER_ID, 5L)).thenReturn(null);
        when(cartRepository.addItem(USER_ID, 5L, "Keyboard", new BigDecimal("149.99"), 3))
                .thenReturn(expectedItem);

        CartItem result = cartUseCaseService.addToCart(5L, 3);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Keyboard");
        assertThat(result.getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldUpdateQuantityWhenItemExists() {
        Product product = Product.builder()
                .id(5L).name("Keyboard").price(new BigDecimal("149.99")).build();
        CartItem existing = new CartItem(1L, USER_ID, 5L, "Keyboard", new BigDecimal("149.99"), 2);
        CartItem updated = new CartItem(1L, USER_ID, 5L, "Keyboard", new BigDecimal("149.99"), 5);

        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(productUseCase.getProductById(5L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserAndProduct(USER_ID, 5L))
                .thenReturn(existing)
                .thenReturn(updated);

        CartItem result = cartUseCaseService.addToCart(5L, 3);

        assertThat(result.getQuantity()).isEqualTo(5);
        verify(cartRepository).updateQuantity(1L, 5);
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(productUseCase.getProductById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCaseService.addToCart(99L, 1))
                .isInstanceOf(AiServiceException.class)
                .hasMessageContaining("Product with ID 99 not found");
    }

    @Test
    void shouldRemoveItemFromCart() {
        when(securityContextProvider.getCurrentUserId()).thenReturn(USER_ID);

        cartUseCaseService.removeFromCart(5L);

        verify(cartRepository).removeItem(USER_ID, 5L);
    }
}
