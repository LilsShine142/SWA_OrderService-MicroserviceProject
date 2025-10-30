package com.example.payment.entity;

import com.example.payment.event.CompensationEvent;
import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentCancelledEvent;
import com.example.payment.event.PaymentRefundedEvent;
import com.example.payment.exception.*;
import com.example.payment.valueobject.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root đại diện cho thực thể Payment trong Payment Service.
 */
@Getter
@Setter
public class Payment extends AggregateRoot<PaymentId> {

    // --- Getters (Không có setters) ---
    // --- Các trường bất biến (set 1 lần trong constructor) ---
    private final OrderId orderId;
    private final CustomerId customerId;
    private final BigDecimal amount;

    // --- Các trường có thể thay đổi (thay đổi qua logic) ---
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String failureReason;
    private LocalDateTime transactionStartAt;
    private LocalDateTime transactionEndAt;

    // Saga fields
    private String sagaId;
    private String sagaStep;
    private PaymentSagaStatus sagaStatus;
    private Integer attemptCount;
    private LocalDateTime nextRetryAt;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

    // Hàm khởi tạo riêng tư, chỉ gọi qua Builder
    private Payment(Builder builder) {
        super.setId(builder.paymentId); // Set ID từ lớp cha
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.amount = builder.amount;
        this.paymentStatus = builder.paymentStatus;
        this.transactionId = builder.transactionId;
        this.failureReason = builder.failureReason;
        this.transactionStartAt = builder.transactionStartAt;
        this.transactionEndAt = builder.transactionEndAt;
        this.sagaId = builder.sagaId;
        this.sagaStep = builder.sagaStep;
        this.sagaStatus = builder.sagaStatus;
        this.attemptCount = builder.attemptCount;
        this.nextRetryAt = builder.nextRetryAt;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // --- HÀNH VI & QUY TẮC NGHIỆP VỤ (Business Logic) ---

    /**
     * Khởi tạo payment với các giá trị ban đầu và phát hành sự kiện.
     */
    public void initializePayment() {
        validateInitialPayment();
        setId(PaymentId.generate());
        paymentStatus = PaymentStatus.PENDING;
        sagaStatus = PaymentSagaStatus.STARTED;
        attemptCount = 0;
        sagaId = UUID.randomUUID().toString();
        sagaStep = "PAYMENT_INITIATED";
        transactionStartAt = LocalDateTime.now();
        createdAt = Instant.now();
        updatedAt = Instant.now();

        // Phát hành sự kiện khi payment được khởi tạo
        addEvent(new PaymentCreatedEvent(this));
    }

    public void processPayment(String transactionId) {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Chỉ payment PENDING mới có thể xử lý! Current status: " + paymentStatus);
        }

        validatePayment();

        boolean paymentSuccess = simulatePaymentProcessing();

        if (paymentSuccess) {
            completePayment(transactionId);
        } else {
            failPayment("Payment processing failed");
        }
    }

    public void completePayment(String transactionId) {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Chỉ payment PENDING mới có thể hoàn thành! Current status: " + paymentStatus);
        }

        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new PaymentValidationException("Transaction ID không được để trống!");
        }

        paymentStatus = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        transactionEndAt = LocalDateTime.now();
        sagaStatus = PaymentSagaStatus.COMPLETED;
        sagaStep = "PAYMENT_COMPLETED";
        updatedAt = Instant.now();
    }

    public void failPayment(String failureReason) {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Chỉ payment PENDING mới có thể thất bại! Current status: " + paymentStatus);
        }
        paymentStatus = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        transactionEndAt = LocalDateTime.now();
        sagaStatus = PaymentSagaStatus.FAILED;
        sagaStep = "PAYMENT_FAILED";
        updatedAt = Instant.now();
    }

    /**
     * Hủy payment và phát hành sự kiện hủy.
     */
    public void cancelPayment(List<String> failureReasons) {
        if (paymentStatus == PaymentStatus.COMPLETED || paymentStatus == PaymentStatus.REFUNDED) {
            throw new PaymentProcessingException("Không thể hủy payment đã hoàn thành hoặc đã hoàn tiền! Current status: " + paymentStatus);
        }
        paymentStatus = PaymentStatus.FAILED;
        this.failureReason = failureReasons != null && !failureReasons.isEmpty() ? String.join(", ", failureReasons) : "Unknown reason";
        transactionEndAt = LocalDateTime.now();
        sagaStatus = PaymentSagaStatus.FAILED;
        sagaStep = "PAYMENT_CANCELLED";
        updatedAt = Instant.now();

        // Phát hành sự kiện khi payment bị hủy
        addEvent(new PaymentCancelledEvent(this));
    }

    /**
     * Kiểm tra xem Payment có thể hoàn tiền không.
     * @return true nếu có thể hoàn tiền (COMPLETED hoặc PENDING), false nếu không.
     */
    public boolean canRefund() {
        return paymentStatus == PaymentStatus.COMPLETED || paymentStatus == PaymentStatus.PENDING;
    }

    /**
     * Thực hiện hoàn tiền và tạo sự kiện.
     * @throws PaymentDomainException nếu không thể hoàn tiền.
     */
    public void performRefund() {
        if (!canRefund()) {
            throw new PaymentDomainException("Payment không thể hoàn tiền ở trạng thái hiện tại: " + paymentStatus);
        }
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.sagaStep = "PAYMENT_REFUNDED";
        this.updatedAt = Instant.now();
        // Tạo và thêm sự kiện hoàn tiền
        this.getEvents().add(new PaymentRefundedEvent(this));
    }

    /**
     * Thực hiện bù trừ (compensation) cho SAGA.
     * @throws PaymentDomainException nếu compensation thất bại.
     */
    public void performCompensation() {
        if (paymentStatus == PaymentStatus.COMPLETED) {
            performRefund();
            this.sagaStatus = PaymentSagaStatus.FAILED;
            this.sagaStep = "PAYMENT_COMPENSATED";
            // Thêm sự kiện bù trừ
            this.getEvents().add(new CompensationEvent(this));
        } else {
            throw new PaymentDomainException("Compensation chỉ áp dụng cho trạng thái COMPLETED: " + paymentStatus);
        }
    }

    public void refundPayment() {
        if (paymentStatus != PaymentStatus.COMPLETED) {
            throw new PaymentProcessingException(
                    "Chỉ payment COMPLETED mới có thể hoàn tiền! Current status: " + paymentStatus);
        }
        paymentStatus = PaymentStatus.REFUNDED;
        sagaStep = "PAYMENT_REFUNDED";
        updatedAt = Instant.now();
    }

    public void compensatePayment() {
        try {
            if (paymentStatus == PaymentStatus.COMPLETED) {
                refundPayment();
                sagaStatus = PaymentSagaStatus.FAILED;
                sagaStep = "PAYMENT_COMPENSATED";
            }
        } catch (Exception e) {
            throw new SagaCompensationException(
                    "Compensation failed for payment: " + getId().getClass(), e);
        }
    }

    public void markForRetry() {
        if (attemptCount >= 3) {
            throw new PaymentProcessingException("Đã vượt quá số lần thử tối đa!");
        }

        this.attemptCount++;
        this.nextRetryAt = LocalDateTime.now().plusMinutes(5);
        this.sagaStatus = PaymentSagaStatus.IN_PROGRESS;
        this.sagaStep = "PAYMENT_RETRY_" + attemptCount;
        this.updatedAt = Instant.now();
    }

    // --- Các quy tắc (Business Rules) nội bộ, dùng private ---

    private void validateInitialPayment() {
        if (paymentStatus != null || getId() != null) {
            throw new PaymentValidationException("Payment đã được khởi tạo rồi!");
        }
    }

    public void validatePayment() {
        validateAmount();
        validateOrderId();
        validateCustomerId();
    }

    private void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentValidationException("Số tiền payment phải lớn hơn 0!");
        }
    }

    private void validateOrderId() {
        if (orderId == null) {
            throw new PaymentValidationException("Order ID không được để trống!");
        }
    }

    private void validateCustomerId() {
        if (customerId == null) {
            throw new PaymentValidationException("Customer ID không được để trống!");
        }
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() > 0.1; // 90% success rate
    }

    // --- Builder Pattern ---
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private PaymentId paymentId;
        private OrderId orderId;
        private CustomerId customerId;
        private BigDecimal amount;
        private PaymentStatus paymentStatus;
        private String transactionId;
        private String failureReason;
        private LocalDateTime transactionStartAt;
        private LocalDateTime transactionEndAt;
        private String sagaId;
        private String sagaStep;
        private PaymentSagaStatus sagaStatus;
        private Integer attemptCount;
        private LocalDateTime nextRetryAt;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder transactionStartAt(LocalDateTime transactionStartAt) {
            this.transactionStartAt = transactionStartAt;
            return this;
        }

        public Builder transactionEndAt(LocalDateTime transactionEndAt) {
            this.transactionEndAt = transactionEndAt;
            return this;
        }

        public Builder sagaId(String sagaId) {
            this.sagaId = sagaId;
            return this;
        }

        public Builder sagaStep(String sagaStep) {
            this.sagaStep = sagaStep;
            return this;
        }

        public Builder sagaStatus(PaymentSagaStatus sagaStatus) {
            this.sagaStatus = sagaStatus;
            return this;
        }

        public Builder attemptCount(Integer attemptCount) {
            this.attemptCount = attemptCount;
            return this;
        }

        public Builder nextRetryAt(LocalDateTime nextRetryAt) {
            this.nextRetryAt = nextRetryAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}