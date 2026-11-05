package com.example.backend.repository;

import com.example.backend.entity.PostForum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostForumRepository extends JpaRepository<PostForum, Integer> {

    // Les posts du forum, du plus récent au plus ancien
    List<PostForum> findAllByOrderByCreatedAtDesc();
}
