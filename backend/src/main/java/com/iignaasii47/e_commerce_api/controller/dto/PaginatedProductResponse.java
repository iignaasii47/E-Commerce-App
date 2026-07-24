package com.iignaasii47.e_commerce_api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated product listing")
public class PaginatedProductResponse {

    @Schema(description = "List of products for the current page")
    private final List<ProductResponse> content;

    @Schema(description = "Total number of products matching the filter", example = "100")
    private final long totalElements;

    @Schema(description = "Total number of pages available", example = "10")
    private final int totalPages;

    @Schema(description = "Zero-based current page index", example = "0")
    private final int currentPage;

    @Schema(description = "Number of items per page", example = "10")
    private final int pageSize;

    public PaginatedProductResponse(List<ProductResponse> content, long totalElements,
                                    int totalPages, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<ProductResponse> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

}
