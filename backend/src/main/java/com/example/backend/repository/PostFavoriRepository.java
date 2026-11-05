package com.example.backend.repository;

import com.example.backend.entity.PostFavori;
import com.example.backend.entity.PostFavoriId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostFavoriRepository extends JpaRepository<PostFavori, PostFavoriId> {

    List<PostFavori> findByUserId(Integer userId);
}
