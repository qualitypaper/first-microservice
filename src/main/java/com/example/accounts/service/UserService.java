package com.example.accounts.service;

import com.example.accounts.controller.dto.ForgetRequest;
import com.example.accounts.controller.dto.LoginRequest;
import com.example.accounts.controller.dto.SignupRequest;
import com.example.accounts.email.EmailService;
import com.example.accounts.model.User;
import com.example.accounts.repository.UserAuthenticationRepository;
import com.example.accounts.repository.UserRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordENcoder;
    @Transactional
    public ResponseEntity<String> signup(SignupRequest signupRequest){
        User user = User.builder()
                .fullName(signupRequest.getFullName())
                .email(signupRequest.getEmail())
                .password(passwordENcoder.encode(signupRequest.getPassword()))
                .build();
        userRepository.save(user);

        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        return ResponseEntity.ok().body(result.toString());
    }

    @Transactional
    public ResponseEntity<String> login(LoginRequest loginRequest){
        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        return ResponseEntity.ok().body(result.toString());
    }

    @Transactional
    public ResponseEntity<String> forgetPassword(ForgetRequest forgetRequest){
        User user = userRepository.findUserByEmail(forgetRequest.getEmail()).get();
        Map<String, String> result = new HashMap<>();
        
        if(user.getUsername() != null){
            // TODO: 3/16/2023 make  
        } else{
            result.put("success", "false");
            return ResponseEntity.ok().body(result.toString());
        }

    }
}
