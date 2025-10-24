package com.example.order.domain.core.valueobject;


import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: StreetAddress
 * Một đối tượng phức hợp, bất biến.
 */
public final class StreetAddress {
    private final UUID id; // ID có thể là của địa chỉ đã lưu của khách hàng
    private final String street;
    private final String postalCode;
    private final String city;

    public StreetAddress(UUID id, String street, String postalCode, String city) {
        this.id = id;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    // Getters
    public UUID getId() { return id; }
    public String getStreet() { return street; }
    public String getPostalCode() { return postalCode; }
    public String getCity() { return city; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreetAddress that = (StreetAddress) o;
        return Objects.equals(street, that.street) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, postalCode, city);
    }
}