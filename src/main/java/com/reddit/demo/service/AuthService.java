package com.reddit.demo.service;

import com.reddit.demo.dto.RegisterRequest;
import com.reddit.demo.exception.SpringRedditException;
import com.reddit.demo.model.NotificationEmail;
import com.reddit.demo.model.User;
import com.reddit.demo.model.VerificationToken;
import com.reddit.demo.repository.UserRepository;
import com.reddit.demo.repository.VerificationTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final MailService mailService;

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
    Long userId = verificationToken.getUser().getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SpringRedditException(
            "User not found with name : " + verificationToken.getUser().getUsername()));
    user.setEnabled(true);
    userRepository.save(user);
  }
}
