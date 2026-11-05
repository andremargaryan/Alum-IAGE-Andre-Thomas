package com.example.backend.repository;

import com.example.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// Requêtes "polymorphiques" : findAll() renvoie à la fois des PostEvenement et des PostForum
public interface PostRepository extends JpaRepository<Post, Integer> {
}
