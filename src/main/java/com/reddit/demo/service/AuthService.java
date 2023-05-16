package com.reddit.demo.service;

import com.reddit.demo.dto.AuthenticationResponse;
import com.reddit.demo.dto.LoginRequest;
import com.reddit.demo.dto.RefreshTokenRequest;
import com.reddit.demo.dto.RegisterRequest;
import com.reddit.demo.exception.SpringRedditException;
import com.reddit.demo.model.NotificationEmail;
import com.reddit.demo.model.User;
import com.reddit.demo.model.VerificationToken;
import com.reddit.demo.repository.UserRepository;
import com.reddit.demo.repository.VerificationTokenRepository;
import com.reddit.demo.security.JwtProvider;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final MailService mailService;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final RefreshTokenService refreshTokenService;

  @Transactional
  public void signup(RegisterRequest registerRequest) {
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreatedDate(Instant.now());
    user.setEnabled(false);

    userRepository.save(user);

    String token = generateVerificationToken(user);

    mailService.sendMail(
        new NotificationEmail("Please Activate your Account", user.getEmail(),
            "Thank you for signing up to Spring Reddit, "
                + "please click on the below url to activate your account : "
                + "http://localhost:8081/api/auth/accountVerification/" + token));
  }

  private String generateVerificationToken(User user) {
    String verificationToken = UUID.randomUUID().toString();
    VerificationToken token = new VerificationToken();
    token.setToken(verificationToken);
    token.setUser(user);

    verificationTokenRepository.save(token);

    return verificationToken;

  }

  public void verifyAccount(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
    verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
    fetchUserAndEnable(verificationToken.get());
  }

  @Transactional
  private void fetchUserAndEnable(VerificationToken verificationToken) {
    String username = verificationToken.getUser().getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new SpringRedditException(
            "User not found with name : " + verificationToken.getUser().getUsername()));
    user.setEnabled(true);
    userRepository.save(user);
  }

  public AuthenticationResponse login(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
            loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtProvider.generateToken(authentication);
    return AuthenticationResponse.builder()
        .authenticationToken(token)
        .refreshToken(refreshTokenService.generateRefreshToken().getToken())
        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
        .username(loginRequest.getUsername()).build();

  }

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByUsername(principal.getSubject()).orElseThrow(
        () -> new UsernameNotFoundException("Username not found : " + principal.getSubject()));
  }

  public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
    String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
    return AuthenticationResponse.builder()
        .authenticationToken(token)
        .refreshToken(refreshTokenRequest.getRefreshToken())
        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
        .username(refreshTokenRequest.getUsername()).build();
  }
}
