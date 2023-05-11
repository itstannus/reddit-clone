package com.reddit.demo.service;

import com.reddit.demo.dto.CommentsDto;
import com.reddit.demo.exception.PostNotFoundException;
import com.reddit.demo.mapper.CommentMapper;
import com.reddit.demo.model.NotificationEmail;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.User;
import com.reddit.demo.repository.CommentRepository;
import com.reddit.demo.repository.PostRepository;
import com.reddit.demo.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

  private static final String POST_URL = "";
  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;
  private final PostRepository postRepository;
  private final AuthService authService;
  private final MailContentBuilder mailContentBuilder;
  private final MailService mailService;
  private final UserRepository userRepository;

  public void save(CommentsDto commentsDto) {
    Post post = postRepository.findById(commentsDto.getPostId())
        .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
    commentRepository.save(commentMapper.map(commentsDto, post, authService.getCurrentUser()));

    String message = mailContentBuilder.build(
        post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
    sendCommentNotification(message, post.getUser());
  }

  private void sendCommentNotification(String message, User user) {
    mailService.sendMail(
        new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(),
            message));
  }

  public List<CommentsDto> getAllCommentsForPost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId.toString()));
    return commentRepository.findAllByPost(post).stream().map(commentMapper::mapToDto)
        .collect(Collectors.toList());
  }

  public List<CommentsDto>  getAllCommentsForUser(String username) {
    User user= userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
    return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto)
        .collect(Collectors.toList());
  }
}
