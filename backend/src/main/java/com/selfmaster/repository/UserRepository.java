package com.selfmaster.repository;

import com.selfmaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailOrUsername(String email, String username);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String token);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
