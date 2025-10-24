package com.example.order.domain.core.valueobject;

import com.example.order.domain.core.event.DomainEvent; // <-- THÊM IMPORT NÀY
import java.util.ArrayList; // <-- THÊM IMPORT NÀY
import java.util.Collections; // <-- THÊM IMPORT NÀY
import java.util.List; // <-- THÊM IMPORT NÀY

/**
 * Cập nhật: Lớp này giờ đây có thể quản lý Domain Events.
 */
public abstract class AggregateRoot<T> extends BaseEntity<T> {

    // SỬA: Thêm một danh sách để chứa các sự kiện
    private final transient List<DomainEvent<?>> domainEvents = new ArrayList<>();

    /**
     * Hàm (protected) để các class con (như Order) thêm sự kiện
     */
    protected void addDomainEvent(DomainEvent<?> event) {
        this.domainEvents.add(event);
    }

    /**
     * Lấy ra danh sách các sự kiện (để lớp Application publish)
     */
    public List<DomainEvent<?>> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Xóa các sự kiện sau khi đã được publish
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}