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
import ru.filit.jirabot.model.type.CustomMsg;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerCommandMessage {

    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;
    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";

    public SendMessage parse(Message message) {
        ChatCommand command = getCommand(message);
        String chatId = message.getChatId().toString();

        switch (command) {
            case SUBSCRIBE:
                return startSubscribe(chatId);
            case SUBSCRIBE_LIST:
                return listSubscribe(chatId);
            case UNSUBSCRIBE:
                return startUnsubscribe(chatId);
            case HELP:
                return messageMapper.formatText(chatId, CustomMsg.HELP.getText());
        }
        return messageMapper.formatText(chatId, CustomMsg.EMPTY.getText());
    }

    private ChatCommand getCommand(Message message) {
        return ChatCommand.findByName(message.getText().split("@")[0]);
    }

    private SendMessage startUnsubscribe(String chatId) {
        log.info("Processing UNSUBSCRIBE for chat: {}", chatId);
        notificationClientApp.addChat(ChatInfo.builder().telegramId(Long.valueOf(chatId)).status(ChatStatus.START_UNSUBSCRIBE.name()).build());
        return messageMapper.formatText(chatId, CustomMsg.START_UNSUB.getText());
    }

    private SendMessage listSubscribe(String chatId) {
        StringBuilder textMessage = new StringBuilder(CustomMsg.SUB_LIST.getText());
        IssueListDto issues = notificationClientApp.getIssues(chatId);
        for (IssueInfo issue : issues.getData()) {
            textMessage.append(String.format(CustomMsg.SUB_LIST_FORMAT.getText(),
                    issue.getCode(),
                    JIRA_URL + issue.getCode(),
                    issue.getStatus()));
        }
        return messageMapper.formatText(chatId, textMessage.toString());
    }

    public SendMessage startSubscribe(String chatId) {
        log.info("Processing SUBSCRIBE for chat: {}", chatId);
        notificationClientApp.addChat(ChatInfo.builder().telegramId(Long.valueOf(chatId)).status(ChatStatus.START_SUBSCRIBE.name()).build());
        return messageMapper.formatText(chatId, CustomMsg.START_SUB.getText());
    }
}
