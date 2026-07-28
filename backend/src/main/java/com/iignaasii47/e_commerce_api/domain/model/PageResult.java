package com.iignaasii47.e_commerce_api.domain.model;

import java.util.List;

public class PageResult<T> {

    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;

    public PageResult(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
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
