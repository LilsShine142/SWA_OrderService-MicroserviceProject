package com.example.order.container;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Main application class to bootstrap the Order Service.
 */
@SpringBootApplication(scanBasePackages = "com.example.order") // Quét tất cả các module
@EnableJpaRepositories(basePackages = "com.example.order.dataaccess.repository") // Chỉ định JpaRepository
@EntityScan(basePackages = "com.example.order.dataaccess.entity") // Chỉ định JPA Entities
public class OrderContainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderContainerApplication.class, args);
	}
}