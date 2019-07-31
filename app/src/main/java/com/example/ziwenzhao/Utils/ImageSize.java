package com.example.ziwenzhao.Utils;

public enum ImageSize {
    size_w92 ("w92"),
    size_w154 ("w154"),
    size_w185 ("w185"),
    size_w342 ("w342"),
    size_w500 ("w500"),
    size_w7780 ("w780"),
    size_original ("original");

    private final String size;

    ImageSize(String size) {
        this.size = size;
    }

    public boolean equalsName(String size) {
        return this.size.equals(size);
    }

    public String toString() {
        return this.size;
    }
}
