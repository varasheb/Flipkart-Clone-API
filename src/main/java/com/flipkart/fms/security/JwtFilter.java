package com.flipkart.fms.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flipkart.fms.entity.AccessToken;
import com.flipkart.fms.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private AccessTokenRepository accessTokenRepo;
	private JwtService jwtservice;
	private CustomUserDetailService userDetailService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String at=null;
		String rt=null;
		Cookie[] cookies=request.getCookies();
		if(cookies!=null) {
			for(Cookie cokiee:cookies) {
				if(cokiee.getName().equals("at")) at=cokiee.getValue();
				if(cokiee.getName().equals("rt")) rt=cokiee.getValue();
			}
			String username=null;
			if(at!=null&&rt!=null) {
				Optional<AccessToken> accessToken = accessTokenRepo.findByTokenAndIsBlocked(at,false);
				if(accessToken==null)throw new RuntimeException();
				else{
					log.info("Authenticating the token.....");
					username=jwtservice.extractUsername(at);
					if(username==null)throw new RuntimeException("Failed to Authentication");
					UserDetails userDetails=userDetailService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null,userDetails.getAuthorities());
					token.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(token);
					log.info("Authentication Sucefully");
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}
