package com.example.common_messaging.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseEvent {
    private String requestId;
    private boolean success;
    private String message;
    private Object data;
}
