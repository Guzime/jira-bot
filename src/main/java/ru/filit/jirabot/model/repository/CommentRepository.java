package ru.filit.jirabot.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.filit.jirabot.model.entity.CommentInfo;

public interface CommentRepository extends JpaRepository<CommentInfo, Integer> {

}
