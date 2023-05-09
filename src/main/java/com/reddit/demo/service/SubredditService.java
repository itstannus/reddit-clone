package com.reddit.demo.service;

import com.reddit.demo.dto.SubredditDto;
import com.reddit.demo.exception.SpringRedditException;
import com.reddit.demo.mapper.SubredditMapper;
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
  private final SubredditMapper subredditMapper;

  @Transactional
  public SubredditDto save(SubredditDto subredditDto) {
    Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
    subredditDto.setId(save.getId());
    return subredditDto;
  }

  @Transactional(readOnly = true)
  public List<SubredditDto> getAll() {
    return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public SubredditDto getSubreddit(Long id) {
    Subreddit subreddit = subredditRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No subreddit found with id  : " + id));
    return subredditMapper.mapSubredditToDto(subreddit);
  }
}
