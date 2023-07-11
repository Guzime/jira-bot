package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.ResponseResult;
import ru.filit.jirabot.model.dto.notification.ResponseNotification;
import ru.filit.jirabot.model.dto.notification.comment.CommentNotificationDto;
import ru.filit.jirabot.model.dto.notification.comment.CommentsNotificationDto;
import ru.filit.jirabot.model.dto.notification.issue.IssueNotificationDto;
import ru.filit.jirabot.model.type.CustomMsg;
import ru.filit.jirabot.model.type.StatusCode;

import java.util.ArrayList;
import java.util.List;

import static ru.filit.jirabot.model.type.CustomMsg.DESCRIPTION_CHANGE;
import static ru.filit.jirabot.model.type.CustomMsg.TITLE_CHANGE;
import static ru.filit.jirabot.model.type.CustomMsg.STATUS_CHANGE;
import static ru.filit.jirabot.model.type.CustomMsg.ADD_COMMENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendlerNotification {
    private static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";
    private final TelegramBot bot;
    private final SendMessageMapper messageMapper;

    public ResponseNotification sendCommentNotification(CommentsNotificationDto notificationDto) {
        List<SendMessage> messages = fetchCommentSendMessages(notificationDto);
        return sendAllMessages(messages);
    }

    public ResponseNotification sendIssueNotification(IssueNotificationDto notificationDto) {
        List<SendMessage> messages = fetchIssueSendMessages(notificationDto);
        return sendAllMessages(messages);
    }

    public List<SendMessage> fetchCommentSendMessages(CommentsNotificationDto notificationDto) {
        List<SendMessage> messages = new ArrayList<>();
        for (Long id : notificationDto.getTelegramsId()) {
            for (CommentNotificationDto comment : notificationDto.getComments()) {
                addSendMessage(messages, id,
                        String.format(ADD_COMMENT.getText(),
                                notificationDto.getCode(),
                                JIRA_URL + notificationDto.getCode(),
                                comment.getAuthor(),
                                comment.getDescription()));
            }
        }
        return messages;
    }

    public List<SendMessage> fetchIssueSendMessages(IssueNotificationDto notificationDto) {
        List<SendMessage> messages = new ArrayList<>();
        for (Long id : notificationDto.getTelegramsId()) {
            if (notificationDto.getChangedDescription()) {
                addSendMessage(messages, id,
                        String.format(DESCRIPTION_CHANGE.getText(),
                                notificationDto.getCode() + " " + notificationDto.getTitle(),
                                JIRA_URL + notificationDto.getCode()));
            }
            if (notificationDto.getChangedTitle()) {
                addSendMessage(messages, id,
                        String.format(TITLE_CHANGE.getText(),
                                notificationDto.getCode(),
                                JIRA_URL + notificationDto.getCode()));
            }
            if (!notificationDto.getStatus().equals(notificationDto.getStatusPrevious())) {
                addSendMessage(messages, id,
                        String.format(STATUS_CHANGE.getText(),
                                notificationDto.getCode(),
                                JIRA_URL + notificationDto.getCode(),
                                notificationDto.getStatusPrevious(),
                                notificationDto.getStatus()));
            }
        }
        return messages;
    }

    public ResponseNotification sendAllMessages(List<SendMessage> messages) {
        try {
            for (SendMessage message : messages) {
                bot.execute(message);
            }
        } catch (TelegramApiException e) {
            log.error("Telegram Error: {}", e.getMessage());
            return new ResponseNotification(new ResponseResult(200, StatusCode.JBOT_007));
        }
        return new ResponseNotification(new ResponseResult(200, StatusCode.JBOT_001));
    }

    private void addSendMessage(List<SendMessage> messages, Long id, String text ) {
        SendMessage message = messageMapper.formatText(String.valueOf(id), text);
        messages.add(message);
        log.info("Add Send message: {}, to chat: {}", message, id);
    }
}
