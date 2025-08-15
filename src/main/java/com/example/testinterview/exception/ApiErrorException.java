package com.example.testinterview.exception;
/**
 * 自定義業務邏輯異常，用於在 Service 層中拋出。
 * 這個異常會攜帶一個 ApiResponseCode 物件，其中包含了錯誤碼和訊息。
 */

public class ApiErrorException extends RuntimeException {
    private final String code;

    public ApiErrorException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
