package com.reddit.demo.repository;

import com.reddit.demo.model.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String refreshToken);

  void deleteByToken(String refreshToken);
}
