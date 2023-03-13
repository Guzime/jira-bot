package ru.filit.jirabot.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.filit.jirabot.config.FeignConfig;
import ru.filit.jirabot.model.dto.jira.JiraIssueInfoResponse;

@FeignClient(name="JiraClientApp", url = "${url.jira.issue.info}", configuration = FeignConfig.class)
public interface JiraClientApp {
    @GetMapping("/{issue}")
    JiraIssueInfoResponse getIssueInfo(@PathVariable("issue") String issue);
}
