package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
public class ProductImageEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private byte[] data;

    @Column(name = "mime_type", nullable = false, length = 50)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public ProductImageEntity(Long productId, byte[] data, String mimeType, Long fileSize, LocalDateTime uploadedAt) {
        this.productId = productId;
        this.data = data;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

}
