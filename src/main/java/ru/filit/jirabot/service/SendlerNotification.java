package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.model.dto.ResponseResult;
import ru.filit.jirabot.model.dto.notification.ResponseNotification;
import ru.filit.jirabot.model.dto.notification.comment.CommentNotificationDto;
import ru.filit.jirabot.model.dto.notification.comment.CommentsNotificationDto;
import ru.filit.jirabot.model.dto.notification.issue.IssueNotificationDto;
import ru.filit.jirabot.model.type.StatusCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendlerNotification {
    private static final Object JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";
    private final TelegramBot bot;

    public ResponseNotification sendCommentNotification(CommentsNotificationDto notificationDto) {
        try {
            for (Long id : notificationDto.getTelegramsId()) {
                List<SendMessage> messages = new ArrayList<>();
                for (CommentNotificationDto comment : notificationDto.getComments()) {
                    messages.add(SendMessage.builder()
                            .chatId(id)
                            .text(String.format("В тикете [%s](%s)\n`%s`\nдобавил _комментарий:_\n `%s`",
                                    notificationDto.getCode(),
                                    JIRA_URL + notificationDto.getCode(),
                                    comment.getAuthor(),
                                    comment.getDescription()))
                            .build());
                }
                for (SendMessage message : messages) {
                    bot.execute(message);
                }
            }
        } catch (TelegramApiException e) {
            return new ResponseNotification(new ResponseResult(200, StatusCode.JBOT_007));
        }
        return new ResponseNotification(new ResponseResult(200, StatusCode.JBOT_001));
    }

    public ResponseNotification sendIssueNotification(IssueNotificationDto notificationDto) {
        return new ResponseNotification(new ResponseResult(200, StatusCode.JBOT_001));
    }
}
