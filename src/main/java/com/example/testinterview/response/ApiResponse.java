package com.example.testinterview.response;

import lombok.EqualsAndHashCode;
import lombok.Data;
import java.util.LinkedHashMap;

/**
 * 統一的 REST API 回應格式，繼承自 LinkedHashMap
 * @param <T> 回應資料的泛型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings("unused")
public class ApiResponse<T> extends LinkedHashMap<String, Object> {

    // 定義 JSON 回應的鍵名
    private static final String CODE_TAG = "code";
    private static final String MESSAGE_TAG = "message";
    private static final String DATA_TAG = "data";

    /**
     * 主要建構子
     * @param code 狀態碼
     * @param message 回應訊息
     * @param data 實際資料
     */
    public ApiResponse(Integer code, String message, T data) {
        super(3); // 初始化容量以提升效能
        super.put(CODE_TAG, code);
        super.put(MESSAGE_TAG, message);
        // 只有在 data 不為 null 時才放入 map
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 建立一個成功的 API 回應
     * @param data 欲回傳的資料
     * @param <T> 資料的泛型
     * @return 包含資料的成功回應
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                ApiResponseCode.SUCCESS.getCode(),
                ApiResponseCode.SUCCESS.getMessage(),
                data
        );
    }

    /**
     * 建立一個不含資料的成功回應
     * @param <T> 資料的泛型 (通常為 Void)
     * @return 不含資料的成功回應
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 建立一個失敗的 API 回應
     * @param code 錯誤代碼 (Enum)
     * @param <T> 資料的泛型 (通常為 Void)
     * @return 不含資料的失敗回應
     */
    public static <T> ApiResponse<T> error(ApiResponseCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }

    /**
     * 建立一個自訂訊息的失敗 API 回應
     * @param code 錯誤代碼
     * @param message 自訂錯誤訊息
     * @param <T> 資料的泛型 (通常為 Void)
     * @return 不含資料的失敗回應
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}