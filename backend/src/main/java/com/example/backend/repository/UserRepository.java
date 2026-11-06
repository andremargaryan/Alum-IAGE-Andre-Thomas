package com.example.backend.repository;

import com.example.backend.entity.StatutUser;
import com.example.backend.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring génère la requête à partir du nom : SELECT ... WHERE email = ?
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByStatut(StatutUser statut);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByIdAndStatutNot(Integer id, StatutUser statut);
}
