package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ProductImageUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.model.Product;
import com.iignaasii47.e_commerce_api.domain.port.out.TokenService;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductUseCase productUseCase;

    @MockitoBean
    private ProductImageUseCase productImageUseCase;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void shouldReturnAllProducts() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        when(productUseCase.getAllProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Keyboard"))
                .andExpect(jsonPath("$[0].price").value(149.99))
                .andExpect(jsonPath("$[0].category").value("peripherals"))
                .andExpect(jsonPath("$[0].image").value("http://img.url"))
                .andExpect(jsonPath("$[0].stock").value(10))
                .andExpect(jsonPath("$[0].rating").value(4.5));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        when(productUseCase.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productUseCase.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterByCategory() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        when(productUseCase.getProductsByCategory("peripherals")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products").param("category", "peripherals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("peripherals"));
    }

    @Test
    void shouldSearchProducts() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        when(productUseCase.searchProducts("keyboard")).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products").param("search", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }

    @Test
    void shouldReturnCategories() throws Exception {
        when(productUseCase.getCategories()).thenReturn(List.of("peripherals", "audio"));

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("peripherals"))
                .andExpect(jsonPath("$[1]").value("audio"));
    }

    @Test
    void shouldReturnImageWhenStoredInDb() throws Exception {
        when(productImageUseCase.getImage(1L))
                .thenReturn(Optional.of(new ImageData(new byte[]{1, 2, 3}, "image/png")));

        mockMvc.perform(get("/api/products/1/image"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }

    @Test
    void shouldFallbackToImageUrlWhenNoStoredImage() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        when(productImageUseCase.getImage(1L)).thenReturn(Optional.empty());
        when(productUseCase.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1/image"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://img.url"));
    }

    @Test
    void shouldReturnNotFoundWhenNoImageAndNoUrl() throws Exception {
        when(productImageUseCase.getImage(1L)).thenReturn(Optional.empty());
        when(productUseCase.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1/image"))
                .andExpect(status().isNotFound());
    }
}
