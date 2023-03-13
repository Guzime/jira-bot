package ru.filit.jirabot.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.filit.jirabot.model.entity.Chat;
import ru.filit.jirabot.model.entity.User;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Optional<Chat> findByTelegramId(String telegramId);
}
