package com.example.testinterview.controller;

import com.example.testinterview.dto.ExchangeRateDto;
import com.example.testinterview.exception.ApiErrorException;
import com.example.testinterview.response.ApiResponse;
import com.example.testinterview.response.ApiResponseCode;
import com.example.testinterview.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 匯率控制器測試類
 * 使用 Mockito 框架模擬依賴關係
 */
@ExtendWith(MockitoExtension.class)
public class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService; // 模擬匯率服務

    @InjectMocks
    private ExchangeRateController exchangeRateController; // 注入被測試的控制器

    /**
     * 測試當服務返回有效數據時，forex 方法應返回成功響應
     */
    @Test
    void forex_shouldReturnSuccess_whenServiceReturnsData() {
        // 安排測試數據
        ExchangeRateDto requestDto = new ExchangeRateDto();
        requestDto.setCurrency("USD");
        requestDto.setStartDate("2024/01/01");
        requestDto.setEndDate("2024/01/10");

        // 創建模擬的匯率數據列表
        List<ExchangeRateDto> responseList = new ArrayList<>();
        ExchangeRateDto dto1 = new ExchangeRateDto();
        dto1.setDate("20240103");
        dto1.setUsdToNtd("31.01");
        ExchangeRateDto dto2 = new ExchangeRateDto();
        dto2.setDate("20240104");
        dto2.setUsdToNtd("31.016");

        responseList.add(dto1);
        responseList.add(dto2);

        // 設置模擬行為：當調用 getExchangeRateHistory 方法時返回預設的響應
        when(exchangeRateService.getExchangeRateHistory(any(ExchangeRateDto.class)))
                .thenReturn(Mono.just(responseList));

        // 執行與驗證
        StepVerifier.create(
                exchangeRateController.forex(requestDto)
                .doOnNext(resp -> {
                    System.out.println("DEBUG success resp: " + resp);
                    System.out.println("DEBUG keys: " + resp.keySet());
                    System.out.println("DEBUG error: " + resp.get("error"));
                    System.out.println("DEBUG currency: " + resp.get("currency"));
                })
        )
        .consumeNextWith(apiResponse -> {
            // 消費並驗證響應內容
            System.out.println("Response received: " + apiResponse);

            // 驗證響應包含必要的鍵
            assert apiResponse.containsKey("error") : "缺少 'error' 鍵";
            assert apiResponse.containsKey("currency") : "缺少 'currency' 鍵";

            // 使用 Optional 和泛型增強型別安全性
            @SuppressWarnings("unchecked")
            Map<String, String> error = Optional.ofNullable(apiResponse.get("error"))
                    .filter(obj -> obj instanceof Map)
                    .map(obj -> (Map<String, String>) obj)
                    .orElseThrow(() -> new AssertionError("錯誤映射為空或格式不正確"));

            assert "0000".equals(error.get("code")) : "預期代碼為 '0000'，但獲得 " + error.get("code");
            assert error.get("message") != null : "消息為空";

            @SuppressWarnings("unchecked")
            List<ExchangeRateDto> data = Optional.ofNullable(apiResponse.get("currency"))
                    .filter(obj -> obj instanceof List)
                    .map(obj -> (List<ExchangeRateDto>) obj)
                    .orElseThrow(() -> new AssertionError("幣種列表為空或格式不正確"));

            assert data.size() == 2 : "預期有 2 個匯率項目，但獲得 " + data.size();
        })
        .verifyComplete(); // 驗證流完成
    }

    /**
     * 測試當服務拋出異常時，forex 方法應返回錯誤響應
     */
    @Test
    void forex_shouldReturnError_whenServiceThrowsException() {
        // 安排測試數據
        ExchangeRateDto requestDto = new ExchangeRateDto();
        requestDto.setCurrency("USD,EUR"); // 設置無效的幣種格式
        requestDto.setStartDate("2024/01/01"); // 設置開始日期
        requestDto.setEndDate("2024/01/10"); // 設置結束日期

        // 設置模擬行為：當調用 getExchangeRateHistory 方法時拋出預設的異常
        when(exchangeRateService.getExchangeRateHistory(any(ExchangeRateDto.class)))
                .thenReturn(Mono.error(new ApiErrorException(
                        ApiResponseCode.INVALID_CURRENCY.getCode(), // 設置錯誤代碼
                        ApiResponseCode.INVALID_CURRENCY.getMessage() // 設置錯誤消息
                )));

        // 執行與驗證
        StepVerifier.create(
                exchangeRateController.forex(requestDto)
                .doOnNext(resp -> {
                    // 調試信息：打印響應詳情
                    System.out.println("DEBUG error resp: " + resp);
                    System.out.println("DEBUG keys: " + resp.keySet());
                    System.out.println("DEBUG error: " + resp.get("error"));
                    System.out.println("DEBUG currency: " + resp.get("currency"));
                })
        )
        .consumeNextWith(apiResponse -> {
            // 消費並驗證響應內容
            System.out.println("Error response received: " + apiResponse);

            // 驗證響應包含必要的鍵
            assert apiResponse.containsKey("error") : "缺少 'error' 鍵";
            assert apiResponse.containsKey("currency") : "缺少 'currency' 鍵";

            // 使用 Optional 和泛型增強型別安全性
            @SuppressWarnings("unchecked")
            Map<String, String> error = Optional.ofNullable(apiResponse.get("error"))
                    .filter(obj -> obj instanceof Map)
                    .map(obj -> (Map<String, String>) obj)
                    .orElseThrow(() -> new AssertionError("錯誤映射為空或格式不正確"));

            assert ApiResponseCode.INVALID_CURRENCY.getCode().equals(error.get("code"))
                  : "預期代碼 " + ApiResponseCode.INVALID_CURRENCY.getCode() + "，但獲得 " + error.get("code");
            assert error.get("message") != null : "消息為空";

            // 驗證匯率數據為空列表
            @SuppressWarnings("unchecked")
            List<?> currency = Optional.ofNullable(apiResponse.get("currency"))
                    .filter(obj -> obj instanceof List)
                    .map(obj -> (List<?>) obj)
                    .orElseThrow(() -> new AssertionError("幣種列表為空或格式不正確"));

            assert currency.isEmpty() : "幣種列表應為空";
        })
        .verifyComplete(); // 驗證流完成
    }
}
