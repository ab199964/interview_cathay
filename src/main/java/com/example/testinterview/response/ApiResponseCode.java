package com.example.testinterview.response;

import lombok.Getter;

/**
 * API 回應狀態碼枚舉
 */
@Getter
public enum ApiResponseCode {

    //================== 通用成功 ==================
    SUCCESS(200, "操作成功"),

    //================== 業務邏輯錯誤碼 (從 -200001 開始) ==================
    INVALID_DATE_RANGE(-200001, "日期區間不符");


    private final int code;
    private final String message;

    ApiResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}