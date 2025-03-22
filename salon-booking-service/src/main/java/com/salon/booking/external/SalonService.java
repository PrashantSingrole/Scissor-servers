package com.salon.booking.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.salon.booking.dto.SalonResponseDto;


@Component
@FeignClient(name = "salon-salon-service", url = "http://localhost:8080/api/salon")
public interface SalonService {
	
	@GetMapping("id")
	public SalonResponseDto fetchSalon(@RequestParam("salonId") int salonId);


}
