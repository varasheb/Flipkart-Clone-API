package com.flipkart.fms.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.requestDTO.AuthRequest;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.AuthResponse;
import com.flipkart.fms.responseDTO.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userrequest);


	ResponseEntity<ResponseStructure<UserResponse>> deleteById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> fetchById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>>  verifyOTP(OtpModel otpmodel);

	ResponseEntity<ResponseStructure<AuthResponse>> userLogin(AuthRequest authRequest,HttpServletResponse httpServletResponse);

	ResponseEntity<ResponseStructure<String>> userLogout(String refreshToken, String acessToken,
			HttpServletResponse response);

	void permantDeleteUser();
	
	void permentDeleteToken();
}
