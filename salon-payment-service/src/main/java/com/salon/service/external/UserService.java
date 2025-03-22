package com.salon.service.external;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.UsersResponseDto;

@Component
@FeignClient(name = "salon-user-service", url = "http://localhost:8080/api/user")
public interface UserService {

	@GetMapping("id")
	public UsersResponseDto fetchUser(@RequestParam("userId") int userId);

	@PutMapping("update/salon/admin")
	public CommanApiResponse updateSalonAdmin(@RequestParam("userId") int userId, @RequestParam("salonId") int salonId);
	
	@PutMapping("update/salon/wallet")
	public CommanApiResponse updateSalonWallet(@RequestParam("userId") int userId, @RequestParam("amountToAdd") BigDecimal amountToAdd);

}
