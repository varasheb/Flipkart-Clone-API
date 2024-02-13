package com.flipkart.fms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.flipkart.fms.Util.SimpleResponseStructure;
import com.flipkart.fms.requestDTO.AuthRequest;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.AuthResponse;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.service.AuthService;

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
	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteById(@PathVariable int userId){
		return authservice.deleteById(userId);
	}
	@GetMapping("/users/{userId}")
	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
	public ResponseEntity<ResponseStructure<UserResponse>> fetchById(@PathVariable int userId){
		return authservice.fetchById(userId);
	}
	
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody @Valid OtpModel otpmodel){
    	return authservice.verifyOTP(otpmodel);
    }
    @PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(@CookieValue(name="rt",required=false) String refreshToken,@CookieValue(name="at",required=false) String accessToken,@RequestBody AuthRequest authRequest, HttpServletResponse httpServletResponse){
		return authservice.userLogin(accessToken,refreshToken,authRequest,httpServletResponse);
	}
    @PostMapping("/logout")
	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
    public ResponseEntity<SimpleResponseStructure> userLogout(@CookieValue(name="rt",required=false) String refreshToken,@CookieValue(name="at",required=false) String acessToken,HttpServletResponse response){
		return authservice.userLogout(refreshToken,acessToken,response);	
    }
    @PostMapping("/revoke-other")
	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
    public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(@CookieValue(name="rt",required=false) String refreshToken,@CookieValue(name="at",required=false) String accessToken,HttpServletResponse response) {
    	return authservice.revokeOtherAccess(refreshToken,accessToken,response);
    }
    @PostMapping("/revoke-All")
	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
    public ResponseEntity<SimpleResponseStructure> revokeAllDevices(HttpServletResponse response) {
    	return authservice.revokeAllAccess(response);
    }
    @PostMapping("/refresh")
   	@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER')")
       public ResponseEntity<SimpleResponseStructure> refresh(@CookieValue(name="rt",required=false) String refreshToken,@CookieValue(name="at",required=false) String accessToken,HttpServletResponse response) {
       	return authservice.refresh(refreshToken,accessToken,response);
       }
    
    
}
