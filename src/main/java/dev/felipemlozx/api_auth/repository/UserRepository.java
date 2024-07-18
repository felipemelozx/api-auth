package dev.felipemlozx.api_auth.repository;

import dev.felipemlozx.api_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
