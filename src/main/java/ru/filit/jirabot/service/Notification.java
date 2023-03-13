package ru.filit.jirabot.service;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.config.JiraClient;
import ru.filit.jirabot.model.entity.Chat;
import ru.filit.jirabot.model.entity.CommentInfo;
import ru.filit.jirabot.model.entity.IssueInfo;
import ru.filit.jirabot.model.repository.CommentRepository;
import ru.filit.jirabot.model.repository.IssueRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class Notification {
    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";
    private final TelegramBot bot;
    private final IssueRepository issueRepository;
    private final JiraClient jiraClient;
    private final CommentRepository commentRepository;

    @Scheduled(fixedDelay = 600000)
    public void scheduleFixedDelayTask() {
        try {
            List<IssueInfo> issues = issueRepository.findAll();
            for (IssueInfo issue : issues) {
                log.info("issue {} is processing", issue.getIssueKey());
                Set<Chat> chats = issue.getSubscribeChats();
                //todo убрать этот костыль!!!
                if (chats.size() == 0) {
                    issueRepository.deleteById(issue.getId());
                    continue;
                }
                Issue issueActual = jiraClient.getIssue(issue.getIssueKey());
                StringBuilder notification = new StringBuilder();
                if (!Objects.equals(issueActual.getDescription(), issue.getDescription())) {
                    log.info("Ticket {} updated description", issue.getIssueKey());
                    notificationDescription(issue, chats, issueActual, notification);
                }
                if (!Objects.equals(issueActual.getStatus().getName(), issue.getStatus())) {
                    log.info("Ticket {} updated status", issue.getIssueKey());
                    notificationStatus(issue, chats, issueActual, notification);
                }
                notificationComment(issue, chats, issueActual, notification);

            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("Fixed delay task - {}", System.currentTimeMillis() / 1000);
    }

    private void notificationComment(IssueInfo issue, Set<Chat> chats, Issue issueActual, StringBuilder notification) throws TelegramApiException {
        for (Comment actualComment : issueActual.getComments()) {
            boolean existComment = isExistComment(issue, actualComment);
            if (!existComment) {
                CommentInfo commentInfo = CommentInfo.builder()
                        .jiraId(Objects.requireNonNull(actualComment.getId()).toString())
                        .author(Objects.requireNonNull(actualComment.getAuthor()).getDisplayName())
                        .description(actualComment.getBody())
                        .build();
                issue.getComments().add(commentInfo);
                //todo РАСКРМЕНТИРУЙ, тут апдейтим запись в БД!!!!!
                issueRepository.save(issue);
                log.info("Comment is added {}", commentInfo);

                notification.append(String.format("В тикете [%s](%s)\n`%s`\nдобавил _комментарий:_\n `%s`",
                        issue.getIssueKey(),
                        JIRA_URL + issue.getIssueKey(),
                        commentInfo.getAuthor(),
                        commentInfo.getDescription()));
                sendNotification(chats, notification);
            }
        }
    }

    private static boolean isExistComment(IssueInfo issue, Comment actualComment) {
        boolean existComment = false;
        log.info("Comment processing: {}", actualComment);
        for (CommentInfo issueComment : issue.getComments()) {
            if (issueComment.getJiraId().equals(Objects.requireNonNull(actualComment.getId()).toString())) {
                existComment = true;
                log.info("Comment with: {} is find", actualComment.getId());
            }
        }
        return existComment;
    }

    private void notificationStatus(IssueInfo issue, Set<Chat> chats, Issue issueActual, StringBuilder notification) throws TelegramApiException {
        notification.append(String.format("*Статус тикета* [%s](%s) *поменяли!* \n\nБыло: _%s_ \nСтало: _%s_",
                issue.getIssueKey(),
                JIRA_URL + issue.getIssueKey(),
                issue.getStatus(),
                issueActual.getStatus().getName()));
        //todo РАСКРМЕНТИРУЙ, тут апдейтим запись в БД!!!!!
        issue.setStatus(issueActual.getStatus().getName());
        issueRepository.save(issue);
        sendNotification(chats, notification);
        notification.setLength(0);
    }

    private void notificationDescription(IssueInfo issue, Set<Chat> chats, Issue issueActual, StringBuilder notification) throws TelegramApiException {
        notification.append(String.format("*Описание тикета* [%s](%s) *поменяли!*",
                issue.getIssueKey(),
                JIRA_URL + issue.getIssueKey()));
        //appendDifference(issue, issueActual, notification);
        //notification.append(String.format("\nПолное описание тикета: \uD83D\uDC47 \uD83D\uDC47 \uD83D\uDC47 \n`%s`", issueActual.getDescription()));
        issue.setDescription(issueActual.getDescription());
        //todo РАСКРМЕНТИРУЙ, тут апдейтим запись в БД!!!!!
        issueRepository.save(issue);
        sendNotification(chats, notification);
        notification.setLength(0);
    }

    private static void appendDifference(IssueInfo issue, Issue issueActual, StringBuilder notification) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(issue.getDescription(), Objects.requireNonNull(issueActual.getDescription()), false);
        for (DiffMatchPatch.Diff diff : diffs) {
            if (diff.text.length() > 1) {
                if (diff.operation == DiffMatchPatch.Operation.INSERT) {
                    notification.append(String.format("Добавили✅  _%s_\n", diff.text));
                    log.info("INSERT is: {}", diff.text);
                }
                if (diff.operation == DiffMatchPatch.Operation.DELETE) {
                    log.info("DELETE is: {}", diff.text);
                    notification.append(String.format("Убрали\uD83D\uDEAB  _%s_\n", diff.text));
                }
            }
        }
    }

    private void sendNotification(Set<Chat> chats, StringBuilder notification) throws TelegramApiException {
        if (notification.length() > 1) {
            for (Chat chat : chats) {
                bot.execute(SendMessage.builder()
                        .chatId(chat.getTelegramId())
                        .parseMode("Markdown")
                        .text(notification.toString())
                        .build());
            }
        }
    }

}
