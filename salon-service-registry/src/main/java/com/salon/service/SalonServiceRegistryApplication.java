package com.salon.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SalonServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalonServiceRegistryApplication.class, args);
	}

}
