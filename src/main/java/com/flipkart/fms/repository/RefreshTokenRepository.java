package com.flipkart.fms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.fms.entity.RefreshToken;
import com.flipkart.fms.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String rt);

	List<RefreshToken> findAllByExpirationBefore(LocalDateTime now);

	List<RefreshToken>  findByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String rt);

	List<RefreshToken>  findByUserAndIsBlockedAndToken(User user, boolean b, String refreshToken);

	List<RefreshToken>  findByUserAndIsBlocked(User user, boolean b);

}
