package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.application.port.in.CartUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolCall;
import com.iignaasii47.e_commerce_api.domain.model.ChatToolResult;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.ChatToolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChatToolExecutorImpl implements ChatToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(ChatToolExecutorImpl.class);

    private final ProductUseCase productUseCase;
    private final CartUseCase cartUseCase;

    public ChatToolExecutorImpl(ProductUseCase productUseCase, CartUseCase cartUseCase) {
        this.productUseCase = productUseCase;
        this.cartUseCase = cartUseCase;
    }

    @Override
    public ChatToolResult execute(ChatToolCall toolCall, Long userId) {
        String name = toolCall.getFunctionName();
        Map<String, Object> args = toolCall.getArguments();
        log.info("Executing tool '{}' for user {}", name, userId);

        try {
            String result = switch (name) {
                case "search_products" -> executeSearchProducts(args);
                case "get_product" -> executeGetProduct(args);
                case "list_categories" -> executeListCategories();
                case "add_to_cart" -> executeAddToCart(args, userId);
                case "remove_from_cart" -> executeRemoveFromCart(args, userId);
                case "view_cart" -> executeViewCart(userId);
                default -> "Unknown tool: " + name;
            };
            return new ChatToolResult(toolCall.getId(), name, result);
        } catch (Exception e) {
            log.error("Tool '{}' execution failed", name, e);
            return new ChatToolResult(toolCall.getId(), name, "Error: " + e.getMessage());
        }
    }

    private String executeSearchProducts(Map<String, Object> args) {
        String query = args.get("query").toString();
        List<Product> products = productUseCase.searchProducts(query);
        return formatProductList(products);
    }

    private String executeGetProduct(Map<String, Object> args) {
        Long id = toLong(args.get("product_id"));
        Optional<Product> product = productUseCase.getProductById(id);
        return product.map(this::formatProduct).orElse("Product not found.");
    }

    private String executeListCategories() {
        List<String> categories = productUseCase.getCategories();
        return "Categories: " + String.join(", ", categories);
    }

    private String executeAddToCart(Map<String, Object> args, Long userId) {
        Long productId = toLong(args.get("product_id"));
        int quantity = args.containsKey("quantity") ? toInt(args.get("quantity")) : 1;
        CartItem item = cartUseCase.addToCart(userId, productId, quantity);
        return "Added " + item.getProductName() + " (x" + item.getQuantity() + ") to cart. "
                + "Cart item ID: " + item.getId();
    }

    private String executeRemoveFromCart(Map<String, Object> args, Long userId) {
        Long cartItemId = toLong(args.get("cart_item_id"));
        cartUseCase.removeFromCart(userId, cartItemId);
        return "Removed cart item " + cartItemId + " from your cart.";
    }

    private String executeViewCart(Long userId) {
        List<CartItem> items = cartUseCase.getCart(userId);
        if (items.isEmpty()) {
            return "Your cart is empty.";
        }
        String itemList = items.stream()
                .map(i -> "- " + i.getProductName() + " x" + i.getQuantity()
                        + " ($" + i.getSubtotal() + ") [ID: " + i.getId() + "]")
                .collect(Collectors.joining("\n"));
        return "Your cart:\n" + itemList;
    }

    private String formatProductList(List<Product> products) {
        if (products.isEmpty()) {
            return "No products found.";
        }
        return products.stream()
                .map(this::formatProduct)
                .collect(Collectors.joining("\n"));
    }

    private String formatProduct(Product p) {
        return "[ID: " + p.getId() + "] " + p.getName()
                + " - $" + p.getPrice() + " (" + p.getCategory() + ") Stock: " + p.getStock();
    }

    private Long toLong(Object value) {
        if (value instanceof Integer i) return i.longValue();
        if (value instanceof Long l) return l;
        return Long.parseLong(value.toString());
    }

    private int toInt(Object value) {
        if (value instanceof Integer i) return i;
        if (value instanceof Long l) return l.intValue();
        return Integer.parseInt(value.toString());
    }

}
