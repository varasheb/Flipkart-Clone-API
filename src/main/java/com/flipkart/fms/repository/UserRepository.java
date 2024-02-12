package com.flipkart.fms.repository;

import org.springframework.stereotype.Repository;

import com.flipkart.fms.entity.User;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{

	Optional<User> findByEmail(String email);

	List<User> findByIsDeleted(boolean b);

	List<User> findByIsEmailVerified(boolean b);

	Optional<User> findByUsername(String username);

	boolean existsByEmail(String email);

}
