package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.CartRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.SecurityContextProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartUseCaseService implements CartUseCase {

    private final CartRepository cartRepository;
    private final ProductUseCase productUseCase;
    private final SecurityContextProvider securityContextProvider;

    public CartUseCaseService(CartRepository cartRepository, ProductUseCase productUseCase,
                               SecurityContextProvider securityContextProvider) {
        this.cartRepository = cartRepository;
        this.productUseCase = productUseCase;
        this.securityContextProvider = securityContextProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCart() {
        Long userId = securityContextProvider.getCurrentUserId();
        return cartRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public CartItem addToCart(Long productId, int quantity) {
        Long userId = securityContextProvider.getCurrentUserId();
        Optional<Product> productOpt = productUseCase.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new AiServiceException("Product with ID " + productId + " not found");
        }
        Product product = productOpt.get();

        CartItem existing = cartRepository.findByUserAndProduct(userId, productId);
        if (existing != null) {
            int newQuantity = existing.getQuantity() + quantity;
            cartRepository.updateQuantity(existing.getId(), newQuantity);
            return cartRepository.findByUserAndProduct(userId, productId);
        }

        return cartRepository.addItem(userId, productId, product.getName(), product.getPrice(), quantity);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartRepository.removeItem(cartItemId);
    }

}
