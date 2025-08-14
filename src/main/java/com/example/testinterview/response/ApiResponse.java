package com.example.testinterview.response;

import lombok.Data;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unused")
public class ApiResponse<T> extends LinkedHashMap<String, Object> {

    private static final String ERROR_TAG = "error";
    private static final String CURRENCY_TAG = "currency";

    /**
     * 成功回應
     */
    public ApiResponse(String code, String message, List<T> currency) {
        super(2);
        super.put(ERROR_TAG, Map.of(
                "code", code,
                "message", message
        ));
        super.put(CURRENCY_TAG, currency);
    }

    /**
     * 建立成功回應
     */
    public static <T> ApiResponse<T> success(List<T> currency) {
        return new ApiResponse<>("0000", "成功 ", currency);
    }

    /**
     * 建立錯誤回應
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, List.of());
    }
}
