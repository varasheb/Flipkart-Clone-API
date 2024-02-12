package com.flipkart.fms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.fms.entity.AccessToken;


public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByToken(String at);

    List<AccessToken> findAllByExpirationBefore(LocalDateTime now);

}
