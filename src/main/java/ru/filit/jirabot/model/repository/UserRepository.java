package ru.filit.jirabot.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.filit.jirabot.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserId(String userId);
}
