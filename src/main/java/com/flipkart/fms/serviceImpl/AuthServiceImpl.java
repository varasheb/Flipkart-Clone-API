package com.flipkart.fms.serviceImpl;

import java.util.Optional;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.cache.CacheStore;
import com.flipkart.fms.entity.Customer;
import com.flipkart.fms.entity.Seller;
import com.flipkart.fms.entity.User;
import com.flipkart.fms.exception.UserAlreadyExistException;
import com.flipkart.fms.exception.UserNotFoundByIdException;
import com.flipkart.fms.repository.UserRepository;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.service.AuthService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepo;
	private PasswordEncoder encoded;
	private CacheStore<String> otpCacheStore;
	private CacheStore<User> userCacheStore;
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userrequest) {
		
		if(userRepo.existsByEmail(userrequest.getEmail())) throw new UserAlreadyExistException("Email is Already Taken");
		 String OTP=generateOTP();
		 User user=mapToUser(userrequest);

		 userCacheStore.add(userrequest.getEmail(), user);
		 otpCacheStore.add(userrequest.getEmail(), OTP);
		
		ResponseStructure<UserResponse> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.ACCEPTED.value());
		structure.setMessage("Please verify through OTP :"+OTP);
		structure.setData(mapToUserResponce(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.ACCEPTED);
		
	}
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>>  verifyOTP(OtpModel otpmodel) {
//		String exOTP =otpCacheStore.get(otpmodel.getEmail());
//		if(exOTP!=null) {
//		if(exOTP.equals(otpmodel.getOtp()))return new ResponseEntity<String>(exOTP,HttpStatus.OK);
//			return  new ResponseEntity<String>("Invalid OTP",HttpStatus.OK);
//		}else
//			return new ResponseEntity<String>("Otp is Expired",HttpStatus.OK);
		User user=userCacheStore.get(otpmodel.getEmail());
		String otp =otpCacheStore.get(otpmodel.getEmail());
		if(otp==null) throw new IllegalArgumentException("OTP is expired!!"); 
		if(user==null) throw new IllegalArgumentException("Registration Session expired!!"); 
			if(otp.equals(otpmodel.getOtp())) {
				user.setEmailVerified(true);
				userRepo.save(user);
				ResponseStructure<UserResponse> structure = new ResponseStructure<>();
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMessage("Sucefully Saved the User");
				structure.setData(mapToUserResponce(user));
				return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
			}else  throw new IllegalAccessError("Invalid OTP!!");
	}
	private String generateOTP() {
		return String.valueOf(new Random().nextInt(10000,999999));
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
		user.setPassword(encoded.encode(userRequest.getPassword()));
		user.setUserRole(userRequest.getUserRole());
		return (T) user;
	}

	public UserResponse mapToUserResponce(User user) {

		return UserResponse.builder().userId(user.getUserId()).username(user.getUsername()).email(user.getEmail())
				.userRole(user.getUserRole()).build();

	}

	@Override
	public void permantDelete() {
		userRepo.findByIsDeleted(true).forEach(user -> {
	            userRepo.delete(user);
	    });
		userRepo.findByIsEmailVerified(false).forEach(user -> {
            userRepo.delete(user);
        });
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> fetchById(int userId) {
		User user =userRepo.findById(userId).get();
		if(user!=null) {
		ResponseStructure<UserResponse> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("Found User");
		structure.setData(mapToUserResponce(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
		}else throw new UserNotFoundByIdException("User Not FoundBy Id!!!");
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteById(int userId) {
		User user =userRepo.findById(userId).get();
		if(user!=null) {
		user.setDeleted(true);
		user=userRepo.save(user);
		ResponseStructure<UserResponse> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("Sucefully Deleted User");
		structure.setData(mapToUserResponce(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
		}else throw new UserNotFoundByIdException("User Not FoundBy Id!!!");
	}

}
