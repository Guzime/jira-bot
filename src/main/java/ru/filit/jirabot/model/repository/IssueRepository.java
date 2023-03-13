package ru.filit.jirabot.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.filit.jirabot.model.entity.Chat;
import ru.filit.jirabot.model.entity.IssueInfo;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<IssueInfo, Integer> {

    Optional<IssueInfo> findByIssueKey(String issueKey);
}
