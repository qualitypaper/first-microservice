package com.example.accounts.service;

import com.example.accounts.config.jwt.JwtResponse;
import com.example.accounts.config.jwt.JwtService;
import com.example.accounts.controller.dto.ForgetRequest;
import com.example.accounts.controller.dto.LoginRequest;
import com.example.accounts.controller.dto.SignupRequest;
import com.example.accounts.controller.dto.UpdatePasswordRequest;
import com.example.accounts.email.EmailService;
import com.example.accounts.model.ForgetPasswordToken;
import com.example.accounts.model.User;
import com.example.accounts.model.UserAuthentication;
import com.example.accounts.repository.UserAuthenticationRepository;
import com.example.accounts.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordENcoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Transactional
    public ResponseEntity<Map<String, String>> signup(SignupRequest signupRequest){
        User user = User.builder()
                .fullName(signupRequest.getFullName())
                .email(signupRequest.getEmail())
                .password(passwordENcoder.encode(signupRequest.getPassword()))
                .build();
        userRepository.save(user);

        String token = tokenService.generateToken();
        emailService.send(user.getEmail(), buildEmail(user.getFullName(), "activate your account", "Activate now", "localhost:8080/auth/confirm?token" + token), "Email Confirmation");
        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        result.put("token", token);
        return ResponseEntity.ok().body(result);
    }

    @Transactional
    public ResponseEntity<JwtResponse> login(LoginRequest loginRequest){
        Authentication authentication  = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = userRepository.findUserByEmail(loginRequest.getEmail()).get();
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        String jwtToken  = jwtService.generateToken(claims, user);
        userAuthenticationRepository.save(new UserAuthentication(null, jwtToken, LocalDateTime.now(), user));

        return ResponseEntity.ok().body(new JwtResponse(jwtToken));
    }

    @Transactional
    public ResponseEntity<Map<String, String>> forgetPassword(ForgetRequest forgetRequest){
        User user = userRepository.findUserByEmail(forgetRequest.getEmail()).get();
        Map<String, String> result = new HashMap<>();
        
        if(user.getUsername() != null){
            String token = tokenService.generateToken();
            tokenService.addPasswordForgetToken(new ForgetPasswordToken(null, token, LocalDateTime.now(), null, user));
            emailService.send(user.getEmail(), buildEmail(user.getFullName(), "reset your password", "Reset now", "localhost:8080/reset?token=" + token), "Password reset");

            result.put("success", "true");
            result.put("token", token);
            return ResponseEntity.ok().body(result);
        } else{
            result.put("success", "false");
            return ResponseEntity.ok().body(result);
        }
    }

    public ResponseEntity<?> updatePassword(UpdatePasswordRequest request){
        // TODO: 3/16/2023 finish method
    }

    public ResponseEntity<HashMap<String, String>> checkUserSession(String token){
        HashMap<String, String> result = new HashMap<>();
        if(jwtService.isTokenExpired(token)){
            result.put("logged_in", "false");
            return ResponseEntity.ok().body(result);
        } else{
            result.put("logged_in", "true");
            return ResponseEntity.ok().body(result);
        }
    }

    private String buildEmail(String name, String reason, String buttonText,  String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to "+reason+": </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">"+ buttonText +"</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
