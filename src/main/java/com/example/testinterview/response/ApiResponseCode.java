package com.example.testinterview.response;

import lombok.Getter;

/**
 * API 回應狀態碼枚舉
 */
@Getter
public enum ApiResponseCode {

    //================== 通用成功 ==================
    SUCCESS("200", "操作成功"),

    //================== 業務邏輯錯誤碼 (從  開始) ==================
    INVALID_DATE_RANGE("E001","日期區間不符"),

    BUSINESS_LOGIC("33553306", "再度踏上錯誤的輪迴"),


    INVALID_CURRENCY("33553307", "幣種未指定或一次查多種,無法踏上新的輪迴"),;



    private final String code;
    private final String message;

    ApiResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}