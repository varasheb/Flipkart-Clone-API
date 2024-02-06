package com.flipkart.fms.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.entity.Customer;
import com.flipkart.fms.entity.Seller;
import com.flipkart.fms.entity.User;
import com.flipkart.fms.repository.UserRepository;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.service.AuthService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepo;

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userrequest) {
		User user = userRepo.save(mapToUser(userrequest));

		ResponseStructure<UserResponse> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("Sucefully saved User");
		structure.setData(mapToUserResponce(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
	}

	public <T extends User> T mapToUser(UserRequest userRequest) {
		User user = null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER -> {
			user = new Customer();
		}
		case SELLER -> {
			user = new Seller();
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + userRequest.getUserRole());
		}
		user.setUsername(userRequest.getEmail().split("@")[0]);
		user.setEmail(userRequest.getEmail());
		user.setPassword(userRequest.getPassword());
		user.setUserRole(userRequest.getUserRole());
		return (T) user;
	}

	public UserResponse mapToUserResponce(User user) {

		return UserResponse.builder().userId(user.getUserId()).username(user.getUsername()).email(user.getEmail())
				.userRole(user.getUserRole()).build();

	}

}
