package com.reddit.demo.service;

import static com.reddit.demo.model.VoteType.UPVOTE;

import com.reddit.demo.dto.VoteDto;
import com.reddit.demo.exception.PostNotFoundException;
import com.reddit.demo.exception.SpringRedditException;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.Vote;
import com.reddit.demo.repository.PostRepository;
import com.reddit.demo.repository.VoteRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class VoteService {

  private final PostRepository postRepository;
  private final VoteRepository voteRepository;
  private final AuthService authService;


  @Transactional
  public void vote(VoteDto voteDto) {
    Post post = postRepository.findById(voteDto.getPostId())
        .orElseThrow(() -> new PostNotFoundException(voteDto.getPostId().toString()));

    Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
        authService.getCurrentUser());

    if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType()
        .equals(voteDto.getVoteType())) {
      throw new SpringRedditException(
          " You have already " + voteDto.getVoteType() + "d for this post");
    }

    if (UPVOTE.equals(voteDto.getVoteType())) {
      post.setVoteCount(post.getVoteCount() + 1);
    } else {
      post.setVoteCount(post.getVoteCount() - 1);
    }

    voteRepository.save(mapToVote(voteDto, post));
    postRepository.save(post);

  }

  private Vote mapToVote(VoteDto voteDto, Post post) {
    return Vote.builder()
        .voteType(voteDto.getVoteType())
        .post(post)
        .user(authService.getCurrentUser())
        .build();
  }
}
