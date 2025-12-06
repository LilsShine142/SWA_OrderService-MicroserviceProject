package com.example.restaurant.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;


/**
 * Main application class to bootstrap the Order Service.
 */
@SpringBootApplication(scanBasePackages = "com.example.restaurant") // Quét tất cả các module
@EnableJpaRepositories(basePackages = "com.example.restaurant.dataaccess.repository") // Chỉ định JpaRepository
@EntityScan(basePackages = "com.example.restaurant.dataaccess.entity") // Chỉ định JPA Entities
@EnableDiscoveryClient
public class RestaurantContainerApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(RestaurantContainerApplication.class, args);
	}
}