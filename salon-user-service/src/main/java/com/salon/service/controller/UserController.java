package com.salon.service.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salon.service.dto.CommanApiResponse;
import com.salon.service.dto.UserLoginRequest;
import com.salon.service.dto.UserLoginResponse;
import com.salon.service.dto.UserRoleResponse;
import com.salon.service.dto.UsersResponseDto;
import com.salon.service.entity.User;
import com.salon.service.service.UserService;
import com.salon.service.utility.Constants.ResponseCode;
import com.salon.service.utility.Constants.Sex;
import com.salon.service.utility.Constants.UserRole;

@RestController
@RequestMapping("api/user/")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@GetMapping("roles")
	public ResponseEntity<?> getAllUsers() {

		UserRoleResponse response = new UserRoleResponse();
		List<String> roles = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			roles.add(role.value());
		}

		if (roles.isEmpty()) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch User Roles");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		else {
			response.setRoles(roles);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("User Roles Fetched success");
			return new ResponseEntity(response, HttpStatus.OK);
		}

	}

	@GetMapping("gender")
	public ResponseEntity<?> getAllUserGender() {

		UserRoleResponse response = new UserRoleResponse();
		List<String> genders = new ArrayList<>();

		for (Sex gender : Sex.values()) {
			genders.add(gender.value());
		}

		if (genders.isEmpty()) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Fetch User Genders");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		else {
			response.setGenders(genders);
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("User Genders Fetched success");
			return new ResponseEntity(response, HttpStatus.OK);
		}

	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody User user) {
		LOG.info("Recieved request for User  register");

		CommanApiResponse response = new CommanApiResponse();
		String encodedPassword = Base64.getEncoder().encodeToString(user.getPassword().getBytes());

		user.setPassword(encodedPassword);
		user.setWalletAmount(BigDecimal.ZERO);

		User registerUser = userService.registerUser(user);

		if (registerUser != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage(user.getRole() + " User Registered Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Register " + user.getRole() + " User");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
		LOG.info("Recieved request for User Login");

		String jwtToken = null;
		UserLoginResponse useLoginResponse = new UserLoginResponse();
		User user = null;

		String encodedPassword = Base64.getEncoder().encodeToString(userLoginRequest.getPassword().getBytes());

		user = this.userService.getUserByEmailIdAndPasswordAndRole(userLoginRequest.getEmailId(), encodedPassword,
				userLoginRequest.getRole());

		if (user == null) {
			useLoginResponse.setResponseCode(ResponseCode.FAILED.value());
			useLoginResponse.setResponseMessage("Invalid Credentials, Failed to login!!!");
			return new ResponseEntity(useLoginResponse, HttpStatus.BAD_REQUEST);
		}

		useLoginResponse = User.toUserLoginResponse(user);

		useLoginResponse.setResponseCode(ResponseCode.SUCCESS.value());
		useLoginResponse.setResponseMessage(user.getFirstName() + " logged in Successful");
		return new ResponseEntity(useLoginResponse, HttpStatus.OK);

	}

	@GetMapping("salon")
	public ResponseEntity<?> fetchAllHotelUsers() {

		UsersResponseDto response = new UsersResponseDto();

		List<User> users = userService.findByRoleAndSalonId(UserRole.SALON.value(), 0);

		if (users == null || users.isEmpty()) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("No Users with Role Hotel found");
		}

		response.setUsers(users);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Salon Users Fetched Successfully");

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@GetMapping("id")
	public ResponseEntity<?> fetchUser(@RequestParam("userId") int userId) {

		UsersResponseDto response = new UsersResponseDto();

		User user = userService.getUserById(userId);

		if (user == null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("No User with this Id present");
		}

		response.setUser(user);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("User Fetched Successfully");

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@PutMapping("update/salon/admin")
	public ResponseEntity<?> updateHotelAdmin(@RequestParam("userId") int userId,
			@RequestParam("salonId") int salonId) {

		CommanApiResponse response = new CommanApiResponse();

		if (userId == 0 && salonId == 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("bad request - missing input");

			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		User user = userService.getUserById(userId);

		if (user == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("user not found!!!");

			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		user.setSalonId(salonId);

		this.userService.updateUser(user);

		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Salon Admin updated successful!!!");

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@PutMapping("update/salon/wallet")
	public ResponseEntity<?> updateSalonWallet(@RequestParam("userId") int userId,
			@RequestParam("amountToAdd") BigDecimal amountToAdd) {

		CommanApiResponse response = new CommanApiResponse();

		if (userId == 0 || amountToAdd == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("bad request - missing input");

			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		if (amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("bad request - amount is less than or equal to 0.0");

			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		User user = userService.getUserById(userId);

		if (user == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("user not found!!!");

			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		}

		user.setWalletAmount(user.getWalletAmount().add(amountToAdd));

		this.userService.updateUser(user);

		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Salon Wallet updated successful!!!");

		return new ResponseEntity(response, HttpStatus.OK);
	}

	@GetMapping("fetch/role-wise")
	public ResponseEntity<?> getAllUsersRolewise(@RequestParam("role") String role) {

		UsersResponseDto response = new UsersResponseDto();

		if (role == null) {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("missing input!!");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<User> users = this.userService.getUsersByRole(role);

		if (CollectionUtils.isEmpty(users)) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage("No Users found!!!");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		response.setUsers(users);
		response.setResponseCode(ResponseCode.SUCCESS.value());
		response.setResponseMessage("Users fetched success");
		return new ResponseEntity(response, HttpStatus.OK);

	}

}
