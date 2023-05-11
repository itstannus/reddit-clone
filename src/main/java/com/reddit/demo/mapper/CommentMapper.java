package com.reddit.demo.mapper;

import com.reddit.demo.dto.CommentsDto;
import com.reddit.demo.model.Comment;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  @Mapping(target="createdDate", expression = "java(java.time.Instant.now())")
  @Mapping(target="post", source="post.")
  @Mapping(target = "user", source = "user")
  @Mapping(target = "text", source = "commentsDto.text")
  @Mapping(target = "id", ignore = true)
  Comment map(CommentsDto commentsDto, Post post, User user);


  @Mapping(target = "postId", expression="java(comment.getPost().getPostId())")
  @Mapping(target = "username", expression="java(comment.getUser().getUsername())")
  CommentsDto mapToDto(Comment comment);

}
