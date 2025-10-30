package com.example.order.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Query) để theo dõi đơn hàng.
 */
public record TrackOrderQuery(
        @NotNull UUID orderTrackingId
) {}