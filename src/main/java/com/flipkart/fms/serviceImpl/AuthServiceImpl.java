package com.flipkart.fms.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkart.fms.Util.CookieManager;
import com.flipkart.fms.Util.MessageStructure;
import com.flipkart.fms.Util.ResponseStructure;
import com.flipkart.fms.Util.SimpleResponseStructure;
import com.flipkart.fms.Util.SimpleResponseStructure;
import com.flipkart.fms.cache.CacheStore;
import com.flipkart.fms.entity.AccessToken;
import com.flipkart.fms.entity.Customer;
import com.flipkart.fms.entity.RefreshToken;
import com.flipkart.fms.entity.Seller;
import com.flipkart.fms.entity.User;
import com.flipkart.fms.exception.UserAlreadyExistException;
import com.flipkart.fms.exception.UserAlreadyLogInException;
import com.flipkart.fms.exception.UserNotFoundByIdException;
import com.flipkart.fms.exception.UserNotLoggedInException;
import com.flipkart.fms.repository.AccessTokenRepository;
import com.flipkart.fms.repository.RefreshTokenRepository;
import com.flipkart.fms.repository.UserRepository;
import com.flipkart.fms.requestDTO.AuthRequest;
import com.flipkart.fms.requestDTO.OtpModel;
import com.flipkart.fms.requestDTO.UserRequest;
import com.flipkart.fms.responseDTO.AuthResponse;
import com.flipkart.fms.responseDTO.UserResponse;
import com.flipkart.fms.security.JwtService;
import com.flipkart.fms.service.AuthService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;

@Slf4j
@Service
//@AllArgsConstructor
//@NoArgsConstructor
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepo;
	private PasswordEncoder encoded;
	private CacheStore<String> otpCacheStore;
	private CacheStore<User> userCacheStore;
	private JavaMailSender javaMailSender;
	private AuthenticationManager authenticationManager;
	private CookieManager cookieManager;
	private JwtService jwtService;
	private AccessTokenRepository accessTokenRepository;
	private RefreshTokenRepository refreshTokenRepository;


	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;
	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSeconds;





	public AuthServiceImpl(UserRepository userRepo, PasswordEncoder encoded, CacheStore<String> otpCacheStore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookieManager, JwtService jwtService, AccessTokenRepository accessTokenRepository,
			RefreshTokenRepository refreshTokenRepository) {
		super();
		this.userRepo = userRepo;
		this.encoded = encoded;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTokenRepository = accessTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
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

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userrequest) {

		if(userRepo.existsByEmail(userrequest.getEmail())) throw new UserAlreadyExistException("Email is Already Taken");
		String OTP=generateOTP();
		User user=mapToUser(userrequest);

		userCacheStore.add(userrequest.getEmail(), user);
		otpCacheStore.add(userrequest.getEmail(), OTP);
		try {
			sendOtpToMail(user, OTP);
		} catch (MessagingException e) {
			log.error("The email address dosen't exist!!!!");
		}
		ResponseStructure<UserResponse> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.ACCEPTED.value());
		structure.setMessage("Please verify through OTP sent on Email:"+user.getEmail()+" OTP:"+OTP);
		structure.setData(mapToUserResponce(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.ACCEPTED);

	}
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>>  verifyOTP(OtpModel otpmodel) {
		User user=userCacheStore.get(otpmodel.getEmail());
		String otp =otpCacheStore.get(otpmodel.getEmail());
		if(otp==null) throw new IllegalArgumentException("OTP is expired!!"); 
		if(user==null) throw new IllegalArgumentException("Registration Session expired!!"); 
		if(otp.equals(otpmodel.getOtp())) {
			user.setEmailVerified(true);
			user=userRepo.save(user);
			try {
				sendMailForSucess(user);
			} catch (MessagingException e) {
				log.error("The email address dosen't exist!!!!");
			}
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
	@Async
	private void sendMail(MessageStructure message) throws MessagingException {
		MimeMessage mimeMessage=javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimeMessage, true);
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText(),true);
		javaMailSender.send(mimeMessage);
	}
	private void sendOtpToMail(User user,String OTP) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail())
				.subject("otp sent form flipkart")
				.sentDate(new Date(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()))
				.text("<h1>Flipkart Registration</h1><br>" +"Hi, "
						+user.getUsername()+" <br>"
						+"Complete your Registration of Flipkart using the OTP<br><br>"
						+"<h1>"+OTP+"</h1>"
						+"<br><br>"
						+"with Best Regards"
						+"Flipkart").build());


	}
	private void sendMailForSucess(User user) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail())
				.subject("flipkart Registration Sucefully")
				.sentDate(new Date(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()))
				.text("<h1>Flipkart Registration</h1><br>" +"Hi, "
						+user.getUsername()+" <br>"
						+"our Registration of Flipkart is Sucefully<br><br>"
						+"<br><br>"
						+"with Best Regards"
						+"Flipkart").build());


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
	public void permantDeleteUser() {
		userRepo.findByIsDeleted(true).forEach(user -> {
			userRepo.delete(user);
		});
		userRepo.findByIsEmailVerified(false).forEach(user -> {
			userRepo.delete(user);
		});
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(String accessToken, String refreshToken,
			AuthRequest authRequest, HttpServletResponse httpServletResponse) {
		if(accessToken!=null && refreshToken!=null) throw new UserAlreadyLogInException("User already Logged in !!!");
		String username = authRequest.getEmail().split("@")[0];
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if(!authentication.isAuthenticated()) {
			throw new UsernameNotFoundException("Failed to authenticate the user");
		}
		else {
			User user1=userRepo.findByUsername(username).map(user->{
				grantAccess(httpServletResponse, user);
				return user;
			}).get();
			ResponseStructure<AuthResponse> authStructure=new ResponseStructure<>();
			authStructure.setStatus(HttpStatus.OK.value());
			authStructure.setMessage("Login success");
			authStructure.setData(mapToAuthResponce(user1));
			return new ResponseEntity(authStructure,HttpStatus.OK);
		}

	}

	public AuthResponse mapToAuthResponce(User user) {
		return AuthResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.userrole(user.getUserRole().name())
				.isAuthenticated(true)
				.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
				.build();
	}

	private void grantAccess(HttpServletResponse response, User user) {

		//		generating access and refresh tokens
		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());

		//		adding access and refresh token in to the response
		response .addCookie(cookieManager.configure(new Cookie("at", accessToken), accessExpiryInSeconds));
		response.addCookie(cookieManager.configure(new Cookie("rt", refreshToken), refreshExpiryInSeconds));

		//		saving the access and refresh cookie in to the database
		accessTokenRepository.save(AccessToken.builder()
				.token(accessToken)
				.isBlocked(false)
				.user(user)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());

		refreshTokenRepository.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.user(user)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
				.build());

	}
	@Override
	public ResponseEntity<SimpleResponseStructure> userLogout(String refreshToken, String accessToken,
			HttpServletResponse response) {
		if(accessToken==null && refreshToken==null) throw new UserNotLoggedInException("User is Not LoggedIn !!");
		accessTokenRepository.findByToken(accessToken).ifPresent(at->{
			at.setBlocked(true);
			accessTokenRepository.save(at);
		});
		refreshTokenRepository.findByToken(refreshToken).ifPresent(rt->{
			rt.setBlocked(true);
			refreshTokenRepository.save(rt);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("rt","")));
		response.addCookie(cookieManager.invalidate(new Cookie("at","")));
		return ResponseEntity.ok(SimpleResponseStructure.builder().status(HttpStatus.OK.value()).message("Logout success").build());
	}

	@Override
	public void permentDeleteToken() {
		List<AccessToken> expiredAccessTokens=accessTokenRepository.findAllByExpirationBefore(LocalDateTime.now());
		expiredAccessTokens.forEach(at->{
			at.setUser(null);
			accessTokenRepository.save(at);
		});
		accessTokenRepository.deleteAll(expiredAccessTokens);
		List<RefreshToken> expiredRefreshTokens=refreshTokenRepository.findAllByExpirationBefore(LocalDateTime.now());
		expiredRefreshTokens.forEach(rt->{
			rt.setUser(null);
			refreshTokenRepository.save(rt);
		});
		refreshTokenRepository.deleteAll(expiredRefreshTokens);
	}
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAllAccess(HttpServletResponse response) {
        userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(user->{
		accessTokenRepository.findByUserAndIsBlocked(user,false).forEach(at->{
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});
		refreshTokenRepository.findByUserAndIsBlocked(user,false).forEach(rt->{
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});	
		});
        response.addCookie(cookieManager.invalidate(new Cookie("rt","")));
		response.addCookie(cookieManager.invalidate(new Cookie("at","")));
		return ResponseEntity.ok(SimpleResponseStructure.builder().status(HttpStatus.OK.value()).message("Revoke  all Device Sucess").build());
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeOtherAccess(String refreshToken, String accessToken,
			HttpServletResponse response) {
        userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(user->{
		accessTokenRepository.findByUserAndIsBlockedAndTokenNot(user,false,accessToken).forEach(at->{
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});
		refreshTokenRepository.findByUserAndIsBlockedAndTokenNot(user,false,refreshToken).forEach(rt->{
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});	
		});
		return ResponseEntity.ok(SimpleResponseStructure.builder().status(HttpStatus.OK.value()).message("Revoke other Device success!!!").build());

	}

	@Override
	public ResponseEntity<SimpleResponseStructure> refresh(String refreshToken, String accessToken,
			HttpServletResponse response) {
		  if (refreshToken == null)throw new UserNotLoggedInException("User is logged out. Please login again.");
		  if (accessToken != null) 
			accessTokenRepository.findByToken(accessToken).ifPresent(at->{
					at.setBlocked(true);
					accessTokenRepository.save(at);
				});   
		    refreshTokenRepository.findByToken(refreshToken).ifPresent(rt->{
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});
			response.addCookie(cookieManager.invalidate(new Cookie("rt","")));
			response.addCookie(cookieManager.invalidate(new Cookie("at","")));
			
		    userRepo.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(user->{
			grantAccess(response, user);
			});
		return ResponseEntity.ok(SimpleResponseStructure.builder().status(HttpStatus.OK.value()).message("Refresh sucefull!!!").build());
	}

}
