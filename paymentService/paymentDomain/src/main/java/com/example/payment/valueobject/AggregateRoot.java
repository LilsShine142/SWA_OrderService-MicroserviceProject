package com.example.payment.valueobject;

import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentDomainEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp trừu tượng đại diện cho Aggregate Root trong Payment Service,
 * quản lý các sự kiện như PaymentCreatedEvent để hỗ trợ SAGA Pattern.
 */
public abstract class AggregateRoot<T> extends BaseEntity<T> {

    // Danh sách tạm thời để chứa các sự kiện
    private final transient List<PaymentDomainEvent<?>> events = new ArrayList<>();

    /**
     * Hàm protected để các lớp con (như Payment) thêm sự kiện.
     * @param event Sự kiện PaymentDomainEvent cần thêm
     */
    protected void addEvent(PaymentDomainEvent<?> event) {
        this.events.add(event);
    }

    /**
     * Lấy ra danh sách các sự kiện (để lớp Application publish).
     * @return Danh sách không thay đổi của các sự kiện
     */
    public List<PaymentDomainEvent<?>> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Xóa các sự kiện sau khi đã được publish.
     */
    public void clearEvents() {
        this.events.clear();
    }
}