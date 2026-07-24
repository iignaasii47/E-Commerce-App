package com.iignaasii47.e_commerce_api.controller;

import com.iignaasii47.e_commerce_api.application.port.in.ProductImageUseCase;
import com.iignaasii47.e_commerce_api.application.port.in.ProductUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ImageData;
import com.iignaasii47.e_commerce_api.domain.model.PageResult;
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
        PageResult<Product> pageResult = TestFixtures.aPageResultOf(product);
        when(productUseCase.getProducts(null, null, 0, 10, "name", "asc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.content[0].price").value(149.99))
                .andExpect(jsonPath("$.content[0].category").value("peripherals"))
                .andExpect(jsonPath("$.content[0].image").value("http://img.url"))
                .andExpect(jsonPath("$.content[0].stock").value(10))
                .andExpect(jsonPath("$.content[0].rating").value(4.5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void shouldReturnSecondPageWithCustomSize() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        PageResult<Product> pageResult = new PageResult<>(List.of(product), 20, 4, 1, 5);
        when(productUseCase.getProducts(null, null, 1, 5, "name", "asc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.totalPages").value(4))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void shouldSortByPriceDescending() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        PageResult<Product> pageResult = TestFixtures.aPageResultOf(product);
        when(productUseCase.getProducts(null, null, 0, 10, "price", "desc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void shouldReturnBadRequestForInvalidSortField() throws Exception {
        mockMvc.perform(get("/api/products").param("sort", "invalid,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidSortDirection() throws Exception {
        mockMvc.perform(get("/api/products").param("sort", "name,up"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForNegativePage() throws Exception {
        mockMvc.perform(get("/api/products").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForZeroSize() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForExcessivePageSize() throws Exception {
        mockMvc.perform(get("/api/products").param("size", "100"))
                .andExpect(status().isBadRequest());
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
        PageResult<Product> pageResult = TestFixtures.aPageResultOf(product);
        when(productUseCase.getProducts(null, "peripherals", 0, 10, "name", "asc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products").param("category", "peripherals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category").value("peripherals"));
    }

    @Test
    void shouldSearchProducts() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        PageResult<Product> pageResult = TestFixtures.aPageResultOf(product);
        when(productUseCase.getProducts("keyboard", null, 0, 10, "name", "asc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products").param("search", "keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"));
    }

    @Test
    void shouldCombineSearchCategorySortAndPagination() throws Exception {
        Product product = TestFixtures.aKeyboardProduct();
        PageResult<Product> pageResult = new PageResult<>(List.of(product), 5, 1, 0, 10);
        when(productUseCase.getProducts("keyboard", "peripherals", 0, 10, "price", "desc"))
                .thenReturn(pageResult);

        mockMvc.perform(get("/api/products")
                        .param("search", "keyboard")
                        .param("category", "peripherals")
                        .param("sort", "price,desc")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.totalElements").value(5));
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
