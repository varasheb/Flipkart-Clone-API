package com.flipkart.fms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.fms.entity.AccessToken;
import com.flipkart.fms.entity.User;


public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByToken(String at);

    List<AccessToken> findAllByExpirationBefore(LocalDateTime now);

	List<AccessToken>  findByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);

	List<AccessToken> findByUserAndIsBlocked(User user, boolean b);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean b);

}
