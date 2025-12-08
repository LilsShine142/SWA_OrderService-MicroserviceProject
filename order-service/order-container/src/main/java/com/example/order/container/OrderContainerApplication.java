package com.example.order.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.TimeZone;

/**
 * Main application class to bootstrap the Order Service.
 */
@SpringBootApplication(scanBasePackages = "com.example.order") // Quét tất cả các module
// SỬA LỖI: Trỏ chính xác đến package chứa JpaRepository interfaces
@EnableJpaRepositories(basePackages = "com.example.order.dataaccess.repository")
@EntityScan(basePackages = "com.example.order.dataaccess.entity")// Chỉ định JPA Entities
@EnableDiscoveryClient
@EnableKafka
public class OrderContainerApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(OrderContainerApplication.class, args);
	}
}