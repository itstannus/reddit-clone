package com.reddit.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;
  @NotBlank(message = "Post Name cannot be empty or Null")
  private String postName;
  @Nullable
  private String url;
  @Nullable
  @Lob
  private String description;
  private Integer voteCount;
  @ManyToOne(fetch= FetchType.LAZY)
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private User user;
  private Instant createdDate;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="id", referencedColumnName = "id")
  private Subreddit subreddit;


}
