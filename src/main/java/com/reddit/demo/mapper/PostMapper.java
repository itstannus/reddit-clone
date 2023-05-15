package com.reddit.demo.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reddit.demo.dto.PostRequest;
import com.reddit.demo.dto.PostResponse;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.Subreddit;
import com.reddit.demo.model.User;
import com.reddit.demo.repository.CommentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

  @Autowired
  private CommentRepository commentRepository;


  @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
  @Mapping(target = "description", source="postRequest.description")
  @Mapping(target = "subreddit", source="subreddit")
  @Mapping(target = "user", source="user")
  @Mapping(target="voteCount", constant = "0")
  public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);


  @Mapping(target = "id", source = "postId")
  @Mapping(target = "userName", source = "user.username")
  @Mapping(target = "subredditName", source = "subreddit.name")
  @Mapping(target = "commentCount", expression = "java(commentCount(post))")
  @Mapping(target = "duration", expression = "java(getDuration(post))" )
  public abstract PostResponse mapToDto(Post post);

  Integer commentCount(Post post){
    return commentRepository.findAllByPost(post).size();
  }

  String getDuration(Post post){
    return TimeAgo.using(post.getCreatedDate().toEpochMilli());
  }

}
