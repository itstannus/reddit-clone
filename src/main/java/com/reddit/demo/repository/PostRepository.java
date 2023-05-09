package com.reddit.demo.repository;

import com.reddit.demo.model.Post;
import com.reddit.demo.model.Subreddit;
import com.reddit.demo.model.User;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByUser(User user);

  List<Post> findBySubreddit(Subreddit subreddit);
}
