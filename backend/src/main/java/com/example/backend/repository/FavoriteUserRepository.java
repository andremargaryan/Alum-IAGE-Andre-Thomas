package com.example.backend.repository;

import com.example.backend.entity.FavoriteUser;
import com.example.backend.entity.FavoriteUserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteUserRepository extends JpaRepository<FavoriteUser, FavoriteUserId> {

    List<FavoriteUser> findByUserId(Integer userId);
}
