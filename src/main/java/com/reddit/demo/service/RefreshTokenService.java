package com.reddit.demo.service;

import com.reddit.demo.dto.RefreshTokenRequest;
import com.reddit.demo.exception.SpringRedditException;
import com.reddit.demo.model.RefreshToken;
import com.reddit.demo.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken generateRefreshToken() {

    RefreshToken refreshToken = RefreshToken.builder()
        .token(UUID.randomUUID().toString())
        .createdDate(Instant.now())
        .build();
    return refreshTokenRepository.save(refreshToken);
  }


  public void validateRefreshToken(String refreshToken) {
    refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new SpringRedditException("Invalid refresh Token"));
  }


  public void deleteRefreshToken(RefreshTokenRequest refreshTokenRequest) {
    refreshTokenRepository.deleteByToken(refreshTokenRequest.getRefreshToken());
  }
}
