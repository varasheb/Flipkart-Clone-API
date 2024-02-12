package com.flipkart.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.fms.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

}
