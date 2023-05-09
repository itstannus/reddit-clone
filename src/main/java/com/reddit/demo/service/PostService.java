package com.reddit.demo.service;

import com.reddit.demo.dto.PostRequest;
import com.reddit.demo.dto.PostResponse;
import com.reddit.demo.exception.PostNotFoundException;
import com.reddit.demo.exception.SubredditNotFoundException;
import com.reddit.demo.mapper.PostMapper;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.Subreddit;
import com.reddit.demo.model.User;
import com.reddit.demo.repository.PostRepository;
import com.reddit.demo.repository.SubredditRepository;
import com.reddit.demo.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class PostService {

  private final SubredditRepository subredditRepository;
  private final AuthService authService;
  private final PostMapper postMapper;
  private final PostRepository postRepository;
  private final UserRepository userRepository;


  public void save(PostRequest postRequest) {
    Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
        .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
    User currentUser = authService.getCurrentUser();
    postRepository.save(postMapper.map(postRequest, subreddit, currentUser));
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getAllPosts() {
    return postRepository.findAll().stream().map(postMapper::mapToDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PostResponse getPost(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(id.toString()));
    return postMapper.mapToDto(post);
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getPostsBySubreddit(Long subredditId) {
    Subreddit subreddit = subredditRepository.findById(subredditId)
        .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
    return postRepository.findBySubreddit(subreddit).stream().map(postMapper::mapToDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getPostByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    return postRepository.findByUser(user).stream().map(postMapper::mapToDto)
        .collect(Collectors.toList());
  }
}
