//package com.example.order.application.dto;
//
//
//// Bạn cần thêm dependency 'spring-boot-starter-validation'
//// vào pom.xml của 'order-application-service' để dùng các @NotNull
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
///**
// * DTO (Command) cho việc tạo đơn hàng[cite: 247].
// * Sử dụng 'record' cho DTO bất biến.
// */
//public record CreateOrderCommand(
//        @NotNull UUID customerId, // [cite: 194]
//        @NotNull UUID restaurantId, // [cite: 195]
//        @NotNull BigDecimal price, // [cite: 196]
//
//        @NotNull @Valid List<OrderItemCommand> items, // [cite: 197]
//        @NotNull @Valid OrderAddress address // [cite: 205]
//) {
//
//    /**
//     * DTO con cho OrderItem
//     */
//    public record OrderItemCommand(
//            @NotNull UUID productId, // [cite: 199]
//            @NotNull Integer quantity, // [cite: 200]
//            @NotNull BigDecimal price, // [cite: 201]
//            @NotNull BigDecimal subTotal // [cite: 202]
//    ) {}
//
//    /**
//     * DTO con cho Address
//     */
//    public record OrderAddress(
//            @NotNull String street, // [cite: 206]
//            @NotNull String postalCode, // [cite: 207]
//            @NotNull String city // [cite: 208]
//    ) {}
//}




package com.example.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderCommand {

    @NotNull(message = "ID của khách hàng không được để trống")
    private UUID customerId;

    @NotNull(message = "ID của nhà hàng không được để trống")
    private UUID restaurantId;

    @NotNull(message = "Tổng tiền đơn hàng không được để trống")
    @DecimalMin(value = "0.01", message = "Tổng tiền phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Tổng tiền không hợp lệ")
    private BigDecimal price;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Size(min = 1, max = 50, message = "Đơn hàng phải có ít nhất 1 và tối đa 50 sản phẩm")
    @Valid // Quan trọng: Để validate sâu vào bên trong từng item
    private List<OrderItemDto> items;

    @NotNull(message = "Địa chỉ giao hàng không được để trống")
    @Valid // Quan trọng: Để validate các trường con của address
    private OrderAddressDto address;

    // --- Inner Class cho Item ---
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        @NotNull(message = "ID sản phẩm không được để trống")
        private UUID productId;

        @Min(value = 1, message = "Số lượng sản phẩm phải ít nhất là 1")
        @Max(value = 1000, message = "Số lượng sản phẩm tối đa là 1000")
        private Integer quantity;

        @NotNull(message = "Giá sản phẩm không được để trống")
        @DecimalMin(value = "0.01", message = "Giá sản phẩm phải lớn hơn 0")
        private BigDecimal price;

        @NotNull(message = "Thành tiền không được để trống")
        @DecimalMin(value = "0.01", message = "Thành tiền phải lớn hơn 0")
        private BigDecimal subTotal;
    }

    // --- Inner Class cho Address ---
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderAddressDto {
        @NotBlank(message = "Tên đường không được để trống")
        @Size(min = 5, max = 255, message = "Tên đường phải từ 5 đến 255 ký tự")
        private String street;

        @NotBlank(message = "Mã bưu điện không được để trống")
        @Size(min = 4, max = 10, message = "Mã bưu điện phải từ 4 đến 10 ký tự")
        private String postalCode;

        @NotBlank(message = "Thành phố không được để trống")
        private String city;
    }
}