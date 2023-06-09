package com.reddit.demo.controller;

import com.reddit.demo.dto.CommentsDto;
import com.reddit.demo.service.CommentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/comments")
@AllArgsConstructor
public class CommentController {

  public final CommentService commentService;


  @PostMapping
  public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
    commentService.save(commentsDto);
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("/by-post/{postId}")
  public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
    return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId));
  }

  @GetMapping("/by-user/{username}")
  public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username) {
    return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(username));
  }

}
