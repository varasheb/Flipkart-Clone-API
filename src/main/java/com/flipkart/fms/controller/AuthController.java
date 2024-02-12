package com.flipkart.fms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.requestDTO.AuthRequest;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.AuthResponse;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/flipkart")
@AllArgsConstructor
public class AuthController {

	private AuthService authservice;
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody @Valid UserRequest userrequest){
		return authservice.registerUser(userrequest);
	}
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteById(@PathVariable int userId){
		return authservice.deleteById(userId);
	}
	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> fetchById(@PathVariable int userId){
		return authservice.fetchById(userId);
	}
	
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody @Valid OtpModel otpmodel){
    	return authservice.verifyOTP(otpmodel);
    }
    @PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(@RequestBody AuthRequest authRequest, HttpServletResponse httpServletResponse){
		return authservice.userLogin(authRequest,httpServletResponse);
	}
    @PostMapping("/logout")
    public ResponseEntity<ResponseStructure<String>> userLogout(@CookieValue(name="rt",required=false) String refreshToken,@CookieValue(name="at",required=false) String acessToken,HttpServletResponse response){
		return authservice.userLogout(refreshToken,acessToken,response);
    	
    }
}
