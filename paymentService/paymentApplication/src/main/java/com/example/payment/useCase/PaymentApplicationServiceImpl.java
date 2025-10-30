package com.example.payment.useCase;

import com.example.payment.dto.CancelPaymentCommand;
import com.example.payment.dto.CancelPaymentResponse;
import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.dto.CreatePaymentResponse;
import com.example.payment.dto.TrackPaymentQuery;
import com.example.payment.dto.TrackPaymentResponse;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.event.*;
import com.example.payment.exception.PaymentProcessingException;
import com.example.payment.mapper.PaymentDataMapper;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.ports.output.MessageBrokerOutputPort;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.valueobject.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payment Application Service Implementation
 */
@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {

    private final PaymentRepository paymentRepository;
    private final MessageBrokerOutputPort messageBrokerOutputPort;
    private final PaymentDataMapper paymentDataMapper;

    public PaymentApplicationServiceImpl(PaymentRepository paymentRepository,
                                         MessageBrokerOutputPort messageBrokerOutputPort,
                                         PaymentDataMapper paymentDataMapper) {
        this.paymentRepository = paymentRepository;
        this.messageBrokerOutputPort = messageBrokerOutputPort;
        this.paymentDataMapper = paymentDataMapper;
    }

    /**
     * Triển khai Use Case: Create Payment
     */
    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentCommand command) {
        // 1. Chuyển DTO -> Domain Entity (dùng Mapper)
        Payment payment = paymentDataMapper.createPaymentCommandToPayment(command);

        // 2. GỌI LOGIC NGHIỆP VỤ (Domain)
        payment.initializePayment();
        PaymentCreatedEvent event = new PaymentCreatedEvent(payment);

        // 3. GỌI CỔNG RA CSDL
        Payment savedPayment = paymentRepository.save(payment);

        // 4. GỌI CỔNG RA MESSAGING (Kafka) - SAGA Pattern
        messageBrokerOutputPort.sendMessage("payment-events", event);
        savedPayment.setSagaStep("PAYMENT_CREATED");
        paymentRepository.update(savedPayment);

        // 5. Chuyển đổi kết quả sang DTO Response để trả về
        return paymentDataMapper.paymentToCreatePaymentResponse(savedPayment,
                "Payment created successfully");
    }

    /**
     * Triển khai Use Case: Cancel Payment
     */
    @Override
    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentCommand command) {

        // 1. Dùng Cổng Ra (Repository) để tìm
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> {

                    return new PaymentNotFoundException("Thanh toán không tồn tại.");
                });

        // 2. GỌI LOGIC NGHIỆP VỤ (Domain)
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            payment.refundPayment();
        } else if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            payment.failPayment(command.failureReason()); // ✅ Sửa thành .failureReason()
        } else {
            throw new PaymentProcessingException("Không thể hủy payment ở trạng thái: " + payment.getPaymentStatus());
        }

        // 3. GỌI CỔNG RA CSDL
        Payment updatedPayment = paymentRepository.update(payment);

        // 4. GỌI CỔNG RA MESSAGING (Kafka) - SAGA Compensating Transaction
        updatedPayment.setSagaStep("PAYMENT_CANCELLED");
        paymentRepository.update(updatedPayment);
        messageBrokerOutputPort.sendPaymentFailed(updatedPayment.getOrderId().toString(), command.failureReason());

        // 5. Map sang DTO Response
        return paymentDataMapper.paymentToCancelPaymentResponse(updatedPayment,
                "Payment cancelled successfully");
    }

    /**
     * Triển khai Use Case: Track Payment
     */
    @Override
    @Transactional(readOnly = true)
    public TrackPaymentResponse trackPayment(TrackPaymentQuery query) {

        // 1. Dùng Cổng Ra (Repository) để tìm
        Payment payment = paymentRepository.findById(query.paymentId())
                .orElseThrow(() -> {

                    return new PaymentNotFoundException("Thanh toán không tồn tại.");
                });

        // 2. Map sang DTO Response
        return paymentDataMapper.paymentToTrackPaymentResponse(payment);
    }

//    /**
//     * Triển khai Use Case: Get Payments By Order
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getPaymentsByOrder(UUID orderId) {
//
//        // 1. Dùng Cổng Ra (Repository) để tìm payments theo order
//        Optional<Payment> payments = paymentRepository.findByOrderId(new OrderId(orderId));
//
//        // 2. Map danh sách Payment entities sang PaymentResponse DTOs
//        List<PaymentResponse> responses = payments.stream()
//                .map(paymentDataMapper::paymentToPaymentResponse)
//                .collect(Collectors.toList());
//
//        return responses;
//    }
}