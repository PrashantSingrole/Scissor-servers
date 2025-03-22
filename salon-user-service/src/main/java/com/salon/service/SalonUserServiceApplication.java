package com.salon.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.salon.service.entity.User;
import com.salon.service.service.UserService;
import com.salon.service.utility.Constants.UserRole;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class SalonUserServiceApplication implements CommandLineRunner {
	
	private final Logger LOG = LoggerFactory.getLogger(SalonUserServiceApplication.class);

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(SalonUserServiceApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {

		User admin = this.userService.getUserByEmailIdAndRole("admin@demo.com", UserRole.ADMIN.value());

		if (admin == null) {

			LOG.info("Admin not found in system, so adding default admin");

			User user = new User();
			user.setEmailId("admin@demo.com");
			user.setPassword(Base64.getEncoder().encodeToString("123456".getBytes()));
			user.setRole(UserRole.ADMIN.value());

			this.userService.registerUser(user);

		}

	}

}
