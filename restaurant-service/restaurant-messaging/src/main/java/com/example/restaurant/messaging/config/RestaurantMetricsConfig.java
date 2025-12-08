//package com.example.restaurant.messaging.config;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RestaurantMetricsConfig {
//
//    @Bean
//    public Counter restaurantOrderApprovedCounter(MeterRegistry meterRegistry) {
//        return meterRegistry.counter("restaurant.order.approved.total");
//    }
//
//    @Bean
//    public Counter restaurantOrderRejectedCounter(MeterRegistry meterRegistry) {
//        return meterRegistry.counter("restaurant.order.rejected.total");
//    }
//}