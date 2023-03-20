package ru.filit.jirabot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.filit.jirabot.config.FeignConfig;
import ru.filit.jirabot.model.dto.chat.ChatDto;
import ru.filit.jirabot.model.dto.chat.ChatInfo;
import ru.filit.jirabot.model.dto.issue.IssueInfo;
import ru.filit.jirabot.model.dto.issue.IssueInfoDto;
import ru.filit.jirabot.model.dto.issue.IssueListDto;

@FeignClient(name="NotificationClientApp", url = "${url.notification-app.chat}", configuration = FeignConfig.class)
public interface NotificationClientApp {
    @GetMapping("/chat/{chatId}")
    ChatDto getChat(@PathVariable("chatId") String chatId);

    @PostMapping("/chat")
    ChatDto addChat(ChatInfo chatInfo);

    @PatchMapping("/chat/{chatId}")
    ChatDto updateChat(@PathVariable("chatId") String chatId, ChatInfo chatInfo);

    @GetMapping("/subscribe/list/{chatId}")
    IssueListDto getIssues(@PathVariable("chatId") String chatId);

    @GetMapping("/unsubscribe/{code}")
    IssueInfoDto unsubscribeIssue(@PathVariable("code") String code);

    @GetMapping("/subscribe/{code}")
    IssueInfoDto subscribeIssue(@PathVariable("code") String code);
}
