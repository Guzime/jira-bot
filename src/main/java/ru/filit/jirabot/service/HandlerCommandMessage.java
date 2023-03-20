package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.dto.issue.IssueInfo;
import ru.filit.jirabot.model.dto.issue.IssueListDto;
import ru.filit.jirabot.model.type.ChatCommand;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.CustomMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerCommandMessage {

    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";

    public SendMessage parse(Message message) {
        ChatCommand command = ChatCommand.findByName(message.getText().split("@")[0]);

        switch (command) {
            case SUBSCRIBE:
                return startSubscribe(message);
            case SUBSCRIBE_LIST:
                return listSubscribe(message);
            case UNSUBSCRIBE:
                return startUnsubscribe(message);
            case HELP:
                return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.HELP_MESSAGE.getText());
        }
        return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.EMPTY_MESSAGE.getText());
    }

    private SendMessage startUnsubscribe(Message message) {
        log.info("Processing UNSUBSCRIBE for chat: {}, {}", message.getChatId(), message.getChat().getTitle());
        notificationClientApp.updateChat(message.getChatId().toString(), ChatInfo.builder().status(ChatStatus.START_UNSUBSCRIBE.name()).build());
        return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.START_UNSUBSCRIBE_MESSAGE.getText());
    }

    private SendMessage listSubscribe(Message message) {
        String textMessage = CustomMessage.SUBSCRIBE_LIST_MESSAGE.getText();
        IssueListDto issues = notificationClientApp.getIssues(message.getChatId().toString());
        for (IssueInfo issue : issues.getData()) {
            textMessage += (String.format(CustomMessage.SUBSCRIBE_LIST_FORMAT_MESSAGE.getText(),
                    issue.getCode(),
                    JIRA_URL + issue.getCode(),
                    issue.getStatus()));
        }
        return messageMapper.customMessage(message.getChatId().toString(), textMessage);
    }

    public SendMessage startSubscribe(Message message) {
        log.info("Processing SUBSCRIBE for chat: {}, {}", message.getChatId(), message.getChat().getTitle());
        notificationClientApp.updateChat(message.getChatId().toString(), ChatInfo.builder().status(ChatStatus.START_SUBSCRIBE.name()).build());
        return messageMapper.customMessage(message.getChatId().toString(), CustomMessage.START_SUBSCRIBE_MESSAGE.getText());
    }
}
