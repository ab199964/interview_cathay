// exception/ApiException.java
package com.example.testinterview.exception;

import com.example.testinterview.response.ApiResponseCode;
import lombok.Getter;

/**
 * 自定義業務邏輯異常，用於在 Service 層中拋出。
 * 這個異常會攜帶一個 ApiResponseCode 物件，其中包含了錯誤碼和訊息。
 */
@Getter
public class ApiException extends RuntimeException {

    private final ApiResponseCode responseCode;

    public ApiException(ApiResponseCode responseCode) {
        // 將錯誤訊息傳遞給父類別 RuntimeException
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}