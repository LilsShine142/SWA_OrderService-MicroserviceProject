package com.example.order.container.exception; // Check package của bạn

import com.example.order.domain.core.exception.OrderDomainException;
import com.example.order.domain.core.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý lỗi Logic nghiệp vụ (Domain Exception) -> Trả về 400 Bad Request
    // Đây là cái sẽ bắt lỗi "Tổng tiền không bằng..."
    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Map<String, Object>> handleOrderDomainException(OrderDomainException e) {
        log.error("Domain Error: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Business Logic Error");
        response.put("message", e.getMessage()); // <--- Lấy message gốc từ Domain đưa ra

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Xử lý lỗi Không tìm thấy -> Trả về 404 Not Found
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFoundException(OrderNotFoundException e) {
        log.error("Not Found Error: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Resource Not Found");
        response.put("message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 3. Xử lý lỗi vi phạm ràng buộc cơ sở dữ liệu (Data Integrity Violation) -> Trả về 409 Conflict
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Data Integrity Error: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Data Integrity Violation");
        response.put("message", "Lỗi ràng buộc dữ liệu: " + e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 4. Xử lý các lỗi còn lại (Unexpected) -> Trả về 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        log.error("Unexpected Error: ", e);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred: " + e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}