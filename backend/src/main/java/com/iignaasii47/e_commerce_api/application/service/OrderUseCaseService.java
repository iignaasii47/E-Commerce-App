package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.OrderUseCase;
import com.iignaasii47.e_commerce_api.domain.exception.EmptyCartException;
import com.iignaasii47.e_commerce_api.domain.exception.InsufficientStockException;
import com.iignaasii47.e_commerce_api.domain.exception.ProductNotFoundException;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.model.Order;
import com.iignaasii47.e_commerce_api.domain.model.OrderItem;
import com.iignaasii47.e_commerce_api.domain.model.OrderStatus;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.CartRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.OrderRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.ProductRepository;
import com.iignaasii47.e_commerce_api.domain.port.out.SecurityContextProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderUseCaseService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final SecurityContextProvider securityContextProvider;

    public OrderUseCaseService(OrderRepository orderRepository, CartRepository cartRepository,
                               ProductRepository productRepository, SecurityContextProvider securityContextProvider) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.securityContextProvider = securityContextProvider;
    }

    @Override
    @Transactional
    public Order createOrder(String shippingAddress, String shippingCity, String shippingZip) {
        Long userId = securityContextProvider.getCurrentUserId();
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cart is empty. Add items before placing an order.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product not found with id: " + cartItem.getProductId()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for " + product.getName()
                                + ". Available: " + product.getStock() + ", requested: " + cartItem.getQuantity());
            }

            OrderItem orderItem = new OrderItem(null, cartItem.getProductId(), cartItem.getProductName(),
                    cartItem.getUnitPrice(), cartItem.getQuantity());
            orderItems.add(orderItem);
            total = total.add(orderItem.getSubtotal());

            productRepository.decrementStock(cartItem.getProductId(), cartItem.getQuantity());
        }

        Order order = orderRepository.createOrder(userId, OrderStatus.CONFIRMED, orderItems, total,
                shippingAddress, shippingCity, shippingZip);

        cartRepository.clearCart(userId);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

}
