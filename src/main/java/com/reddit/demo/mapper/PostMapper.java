package com.reddit.demo.mapper;

import com.reddit.demo.dto.PostRequest;
import com.reddit.demo.dto.PostResponse;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.Subreddit;
import com.reddit.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {


  @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
  @Mapping(target = "description", source="postRequest.description")
  @Mapping(target = "subreddit", source="subreddit")
  @Mapping(target = "user", source="user")
  Post map(PostRequest postRequest, Subreddit subreddit, User user);


  @Mapping(target = "id", source = "postId")
  @Mapping(target = "userName", source = "user.username")
  @Mapping(target = "subredditName", source = "subreddit.name")
  PostResponse mapToDto(Post post);

}
