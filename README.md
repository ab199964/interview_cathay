# 匯率查詢服務 (Exchange Rate Service)

這是一個基於 Spring Boot 的專案，提供歷史匯率查詢的 API。它採用了響應式編程模型 (Reactive Programming) 來處理非同步資料流，並使用 MongoDB 作為資料庫。

## ✨ 功能
- 根據指定日期和幣別，查詢歷史匯率。
- 定時從外部 API 同步最新的匯率資料。
- 提供統一的 API 響應格式。

## 🛠️ 使用技術

- **後端框架**: Spring Boot (WebFlux)
- **程式語言**: Java 17
- **資料庫**: MongoDB (Reactive)
- **專案管理**: Maven
- **輔助工具**:
    - Lombok: 簡化樣板程式碼。
    - MapStruct: 自動化物件之間的映射。

## 🚀 如何開始

請遵循以下步驟來設定和執行此專案。

### 1. 環境需求

- JDK 17 或更高版本
- Maven 3.6+
- 一個正在運行的 MongoDB 實例

### 2. 專案設定

1.  **Clone 專案**
    ```bash
    git clone https://github.com/ab199964/interview_cathay.git
    cd interview_cathay
    ```

2.  **設定資料庫連線**

    在 `src/main/resources/application.properties` 中，設定您的 MongoDB 連線資訊：
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/your_database_name
    ```

### 3. 執行專案

您可以使用 Maven wrapper 來啟動應用程式：

-   **Windows:**
    ```bash
    mvnw spring-boot:run
    ```
-   **macOS/Linux:**
    ```bash
    ./mvnw spring-boot:run
    ```
    
應用程式啟動後，將在 `localhost:8080` 上提供服務。

## 📖 API 端點說明

### 查詢歷史匯率

查詢特定日期的匯率資料。

- **URL**: `/api/exchange-rate/history`
- **Method**: `POST`
- **Content-Type**: `application/json`

#### 請求範例

```json
{
  "date": "2024-12-25",
  "currency": "USD"
}
```

#### 成功響應 (Success Response)

- **HTTP Status**: `200 OK`
- **Body**:
```json
{
  "code": "0000",
  "message": "成功",
  "data": {
    "date": "2024-12-25",
    "usdToNtd": "31.5"
  }
}
```

#### 錯誤響應 (Error Response)

- **HTTP Status**: `200 OK` (業務邏輯錯誤)
- **Body**:
```json
{
  "code": "E001",
  "message": "查無資料",
  "data": null
}
```
