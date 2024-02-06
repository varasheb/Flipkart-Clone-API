package com.flipkart.fms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/flipkart")
@AllArgsConstructor
public class AuthController {

	private AuthService authservice;
	@PostMapping
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody @Valid UserRequest userrequest){
		return authservice.registerUser(userrequest);
	}
	
}
