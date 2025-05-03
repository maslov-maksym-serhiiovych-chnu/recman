package edu.chnu.recman_backend.auth.repositories;

import edu.chnu.recman_backend.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    
    Optional<User> findByUsername(String username);
}
