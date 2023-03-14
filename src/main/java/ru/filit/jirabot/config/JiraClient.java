package ru.filit.jirabot.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
@Component
public class JiraClient {
    private String username;
    private String password;
    private String jiraUrl;
    private JiraRestClient restClient;
/*
    public JiraClient(@Value("${jira.rosbank.username}")String username,
                      @Value("${jira.rosbank.password}")String password,
                      @Value("${jira.rosbank.url}")String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.restClient = getJiraRestClient();
    }

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), this.username, this.password);
    }*/
    private URI getJiraUri() {
        return URI.create(this.jiraUrl);
    }

    public Issue getIssue(String issueKey) {
        return restClient.getIssueClient().getIssue(issueKey).claim();
    }
}
