package com.flipkart.fms.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.flipkart.fms.entity.User;

public class CustomUserDetail implements UserDetails{

	  @Autowired
		private User user;

		public CustomUserDetail(User user) {
			super();
			this.user=user;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name()));
		}

		@Override
		public String getPassword() {
			// TODO Auto-generated method stub
			return user.getPassword();
		}


		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			return user.getUsername();
		}

		@Override
		public boolean isAccountNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return true;
		}

}
