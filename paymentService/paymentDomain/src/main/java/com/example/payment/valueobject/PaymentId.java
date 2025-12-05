//package com.example.payment.valueobject;
//
//import java.util.UUID;
//
///**
// * Value Object đại diện cho ID của Payment
// */
//public record PaymentId(UUID value) {
//
//    /**
//     * Constructor từ UUID - tự động generate nếu null
//     */
//    public PaymentId {
//        value = value != null ? value : UUID.randomUUID();
//    }
//
//    /**
//     * Constructor từ String - tự động generate nếu null/empty
//     */
//    public PaymentId(String value) {
//        this(parseUUID(value));
//    }
//
//    private static UUID parseUUID(String value) {
//        if (value == null || value.trim().isEmpty()) {
//            return UUID.randomUUID();
//        }
//        try {
//            return UUID.fromString(value.trim());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid UUID format: " + value, e);
//        }
//    }
//
//    /**
//     * Tạo PaymentId mới với UUID ngẫu nhiên
//     */
//    public static PaymentId generate() {
//        return new PaymentId(UUID.randomUUID());
//    }
//
//    /**
//     * Trả về giá trị UUID
//     */
//    public UUID getValue() {
//        return value;
//    }
//}




package com.example.payment.valueobject;

import java.util.UUID;

/**
 * Value Object đại diện cho ID của Payment
 */
public record PaymentId(UUID value) {
}