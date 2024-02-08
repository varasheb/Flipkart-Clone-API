package com.flipkart.fms.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.UserResponse;

@Service
public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userrequest);

	void permantDelete();

	ResponseEntity<ResponseStructure<UserResponse>> deleteById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> fetchById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>>  verifyOTP(OtpModel otpmodel);


}
