package com.reddit.demo.mapper;

import com.reddit.demo.dto.SubredditDto;
import com.reddit.demo.model.Post;
import com.reddit.demo.model.Subreddit;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

  @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
  SubredditDto mapSubredditToDto(Subreddit subreddit);

  default Integer mapPosts(List<Post> posts){
    return posts.size();
  }


  // use inverse to map dto to subreddit
  @InheritInverseConfiguration
  @Mapping(target = "posts", ignore = true)
  Subreddit mapDtoToSubreddit(SubredditDto subredditDto);

}
