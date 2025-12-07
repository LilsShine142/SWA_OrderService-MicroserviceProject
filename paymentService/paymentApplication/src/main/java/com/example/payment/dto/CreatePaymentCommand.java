package com.example.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Command) để tạo thanh toán.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentCommand {
    @NotNull(message = "ID đơn hàng không được để trống")
    private UUID orderId;

    @NotNull(message = "ID khách hàng không được để trống")
    private UUID customerId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Số tiền không hợp lệ")
    private BigDecimal amount;

    private LocalDateTime createdAt;
}