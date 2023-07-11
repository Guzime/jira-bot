package ru.filit.jirabot.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.filit.jirabot.mapper.SendMessageMapper;
import ru.filit.jirabot.model.dto.notification.ResponseNotification;
import ru.filit.jirabot.model.dto.notification.issue.IssueNotificationDto;
import ru.filit.jirabot.model.type.StatusCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SendlerNotificationTest {

    private final TelegramBot bot = mock(TelegramBot.class);
    private final SendlerNotification sendlerNotification = new SendlerNotification(bot, new SendMessageMapper());

    @Test
    @DisplayName("Send Message don't execute")
    void test1() throws TelegramApiException {
        IssueNotificationDto request = getIssueAllChangeNotificationDto();
        List<SendMessage> messages = sendlerNotification.fetchIssueSendMessages(request);
        when(bot.execute(messages.get(0))).thenThrow(new TelegramApiException());
        ResponseNotification responseNotification = sendlerNotification.sendAllMessages(messages);

        assertThat(StatusCode.valueOf(responseNotification.getResult().getCode())).isEqualTo(StatusCode.JBOT_007);
    }

    @Test
    @DisplayName("Send Message execute success")
    void test2() {
        IssueNotificationDto request = getIssueAllChangeNotificationDto();
        List<SendMessage> messages = sendlerNotification.fetchIssueSendMessages(request);
        ResponseNotification responseNotification = sendlerNotification.sendAllMessages(messages);

        assertThat(StatusCode.valueOf(responseNotification.getResult().getCode())).isEqualTo(StatusCode.JBOT_001);
    }

    @Test
    @DisplayName("Send Message execute success for other branches")
    void test3() {
        IssueNotificationDto request = getIssueStatusNotificationDto();
        List<SendMessage> messages = sendlerNotification.fetchIssueSendMessages(request);
        ResponseNotification responseNotification = sendlerNotification.sendAllMessages(messages);

        assertThat(StatusCode.valueOf(responseNotification.getResult().getCode())).isEqualTo(StatusCode.JBOT_001);
    }

    @Test
    @DisplayName("Count 4 Send Message for two chats")
    void test4() {
        IssueNotificationDto request = getIssueAllChangeNotificationDto();
        List<SendMessage> messages = sendlerNotification.fetchIssueSendMessages(request);

        assertThat(messages.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Count 2 Send Message for two chats")
    void test5() {
        IssueNotificationDto request = getIssueStatusNotificationDto();
        List<SendMessage> messages = sendlerNotification.fetchIssueSendMessages(request);

        assertThat(messages.size()).isEqualTo(2);
    }

    private IssueNotificationDto getIssueAllChangeNotificationDto() {
        return IssueNotificationDto.builder()
                .telegramsId(new HashSet<>(Arrays.asList(231852649L, -851899839L)))
                .code("IN-387")
                .title("Доступ к топикам на ПРОД")
                .status("Backlog")
                .statusPrevious("Backlog")
                .changedDescription(true)
                .changedTitle(true)
                .build();
    }

    private IssueNotificationDto getIssueStatusNotificationDto() {
        return IssueNotificationDto.builder()
                .telegramsId(new HashSet<>(Arrays.asList(231852649L, -851899839L)))
                .code("IN-387")
                .title("Доступ к топикам на ПРОД")
                .status("Backlog")
                .statusPrevious("Done")
                .changedDescription(false)
                .changedTitle(false)
                .build();
    }
}