package dev.felipemlozx.api_auth.repository;

import dev.felipemlozx.api_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String login);

  List<User> findByVerifiedIsFalse();

}
