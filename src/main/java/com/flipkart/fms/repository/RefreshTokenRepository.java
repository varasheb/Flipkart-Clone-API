package com.flipkart.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.fms.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
