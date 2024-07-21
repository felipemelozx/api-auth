package dev.felipemlozx.api_auth.repository;

import dev.felipemlozx.api_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String login);
}
