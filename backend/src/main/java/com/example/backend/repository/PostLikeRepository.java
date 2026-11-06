package com.example.backend.repository;

import com.example.backend.entity.PostLike;
import com.example.backend.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    // Nombre de likes d'un post
    long countByPostId(Integer postId);
}
