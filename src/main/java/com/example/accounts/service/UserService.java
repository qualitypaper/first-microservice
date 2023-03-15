package com.example.accounts.service;

import com.example.accounts.controller.dto.SignupRequest;
import com.example.accounts.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<String> signup(SignupRequest signupRequest){
        return ResponseEntity.ok().body("asdfasf");
    }
}
