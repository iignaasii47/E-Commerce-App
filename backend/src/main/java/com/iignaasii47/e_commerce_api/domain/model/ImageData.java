package com.iignaasii47.e_commerce_api.domain.model;

public class ImageData {

    private final byte[] data;
    private final String mimeType;

    public ImageData(byte[] data, String mimeType) {
        this.data = data.clone();
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return data.clone();
    }

    public String getMimeType() {
        return mimeType;
    }
}
