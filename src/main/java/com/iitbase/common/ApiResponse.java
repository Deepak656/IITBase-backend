package com.iitbase.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;           // Data first!
    private String message;   // Message second (optional)
    private long timestamp;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }


    // Primary method - data only (most common for success)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, System.currentTimeMillis());
    }

    // With custom message (less common)
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, System.currentTimeMillis());
    }

    // Error responses (message required)
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, System.currentTimeMillis());
    }

    // Error with data (e.g., validation errors)
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, data, message, System.currentTimeMillis());
    }
}