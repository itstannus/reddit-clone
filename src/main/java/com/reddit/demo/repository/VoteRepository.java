package com.reddit.demo.repository;

import com.reddit.demo.model.Post;
import com.reddit.demo.model.User;
import com.reddit.demo.model.Vote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

  //Order by vote id gives the most recent vote done by user
  Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
