package com.example.accounts.repository;

import com.example.accounts.model.ForgetPasswordToken;
import com.example.accounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgetPasswordTokenRepository extends JpaRepository<ForgetPasswordToken, Long> {
    Optional<ForgetPasswordToken> findByUser(User user);
    Optional<ForgetPasswordToken> findByToken(String token);
}
