package ru.filit.jirabot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.filit.jirabot.config.FeignConfig;
import ru.filit.jirabot.model.dto.chat.ChatDto;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.dto.issue.IssueInfoDto;
import ru.filit.jirabot.model.dto.issue.IssueListDto;

@FeignClient(name = "NotificationClientApp", url = "${url.notification-app}", configuration = FeignConfig.class)
public interface NotificationClientApp {
    @GetMapping("/notification/chat/telegram/{chatId}")
    ChatDto getChat(@PathVariable("chatId") String chatId);

    @PatchMapping("/notification/chat")
    ChatDto addChat(ChatInfo chatInfo);

    @GetMapping("/notification/issue/telegram/{chatId}")
    IssueListDto getIssues(@PathVariable("chatId") String chatId);

    @DeleteMapping("/notification/issue/unsubscribe/{telegramId}/{code}")
    IssueInfoDto unsubscribeIssue(@PathVariable("telegramId") Long telegramId, @PathVariable("code") String code);

    @PostMapping("/notification/issue/subscribe/{telegramId}/{code}")
    IssueInfoDto subscribeIssue(@PathVariable("telegramId") Long telegramId, @PathVariable("code") String code);
}
