package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private int status = 200;
    private boolean success = true;
    private String message;
    private Object data;
}
