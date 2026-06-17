package com.pigeonkim.paymentshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
public class PaymentShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentShopApplication.class, args);
	}

}
