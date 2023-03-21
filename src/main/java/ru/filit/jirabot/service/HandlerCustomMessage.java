package ru.filit.jirabot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.filit.jirabot.api.NotificationClientApp;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.ResponseResult;
import ru.filit.jirabot.model.dto.issue.IssueInfoDto;
import ru.filit.jirabot.model.type.ChatStatus;
import ru.filit.jirabot.model.type.CustomMsg;
import ru.filit.jirabot.model.type.StatusCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerCustomMessage {

    private final NotificationClientApp notificationClientApp;
    private final SendMessageMapper messageMapper;

    public static final String JIRA_URL = "https://jirahq.rosbank.rus.socgen:8443/browse/";

    public SendMessage parse(String chatId, ChatStatus Status, String inputMessage) {
        switch (Status) {
            case START_SUBSCRIBE:
                return subscribe(chatId, inputMessage);
            case START_UNSUBSCRIBE:
                return unsubscribe(chatId, inputMessage);
        }
        return emptyMessage(chatId, inputMessage);
    }

    private SendMessage unsubscribe(String chatId, String inputMessage) {
        if (validateIssueCode(inputMessage)) {
            IssueInfoDto issueUnsubscribe = notificationClientApp.unsubscribeIssue(inputMessage);
            if (StatusCode.JBOT_003.equals(getResponseCode(issueUnsubscribe.getResult()))) {
                return issueNotFound(chatId, inputMessage);
            }
            return subscribeSuccess(chatId, inputMessage);
        }
        return validationError(chatId, inputMessage);
    }

    private SendMessage subscribe(String chatId, String inputMessage) {
        if (validateIssueCode(inputMessage)) {
            IssueInfoDto issueSubscribe = notificationClientApp.subscribeIssue(inputMessage);
            StatusCode responseCode = getResponseCode(issueSubscribe.getResult());
            log.info("Response for subscribe: {}", issueSubscribe);

            switch (responseCode) {
                case JBOT_004:
                    return alreadyExistIssue(chatId, inputMessage);
                case JBOT_001:
                    return successSubscribe(chatId, inputMessage, issueSubscribe);
                case JBOT_005:
                    return errorSubscribe(chatId, inputMessage);
            }
        }
        return validationError(chatId, inputMessage);
    }

    private SendMessage emptyMessage(String chatId, String inputMessage) {
        return messageMapper.formatText(chatId, String.format(CustomMsg.EMPTY.getText(), inputMessage));
    }

    private SendMessage subscribeSuccess(String chatId, String inputMessage) {
        log.info("Ticket {} unsubscribe", inputMessage);
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.UNSUB_SUCCESS.getText(), inputMessage));
    }

    private SendMessage issueNotFound(String chatId, String inputMessage) {
        log.info("Ticket {} not found", inputMessage);
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.UNSUB_NOT_FOUND.getText(), inputMessage));
    }

    private boolean validateIssueCode(String inputMessage) {
        return inputMessage.split("-").length == 2;
    }

    private SendMessage validationError(String chatId, String inputMessage) {
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.VALID_ERROR.getText(), inputMessage));
    }

    private StatusCode getResponseCode(ResponseResult result) {
        return StatusCode.valueOf(result.getCode());
    }

    private SendMessage errorSubscribe(String chatId, String inputMessage) {
        log.info("Ticket {} error response", inputMessage);
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.SUB_ERROR.getText(), inputMessage));
    }

    private SendMessage successSubscribe(String chatId, String inputMessage, IssueInfoDto issueSubscribe) {
        log.info("Ticket {} success subscribe", inputMessage);
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.SUB_SUCCESS.getText(),
                        inputMessage,
                        JIRA_URL + inputMessage,
                        issueSubscribe.getData().getStatus()));
    }

    private SendMessage alreadyExistIssue(String chatId, String inputMessage) {
        log.info("Ticket {} already exist", inputMessage);
        return messageMapper.formatText(chatId,
                String.format(CustomMsg.SUB_EXIST.getText(), inputMessage));
    }
}
