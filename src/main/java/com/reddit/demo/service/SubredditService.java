package com.reddit.demo.service;

import com.reddit.demo.dto.SubredditDto;
import com.reddit.demo.model.Subreddit;
import com.reddit.demo.repository.SubredditRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

  private final SubredditRepository subredditRepository;

  @Transactional
  public SubredditDto save(SubredditDto subredditDto) {
    Subreddit save = subredditRepository.save(mapSubredditDto(subredditDto));
    subredditDto.setId(save.getId());
    return subredditDto;
  }

  private Subreddit mapSubredditDto(SubredditDto subredditDto) {
    return Subreddit.builder().name(subredditDto.getName())
        .description(subredditDto.getDescription()).build();
  }

  @Transactional(readOnly = true)
  public List<SubredditDto> getAll() {
    return  subredditRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  private SubredditDto mapToDto(Subreddit subreddit) {
    return SubredditDto.builder().id(subreddit.getId()).name(subreddit.getName()).description(
        subreddit.getDescription()).numberOfPosts(subreddit.getPosts().size()).build();
  }
}
