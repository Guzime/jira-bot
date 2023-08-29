package ru.filit.jirabot.model.dto.notification.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class IssueNotificationDto {
    @JsonProperty("telegramsId")
    private Set<Long> telegramsId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("title")
    private String title;
    @JsonProperty("status")
    private String status;
    @JsonProperty("statusPrevious")
    private String statusPrevious;
    @JsonProperty("changedDescription")
    private Boolean changedDescription;
    @JsonProperty("changedTitle")
    private Boolean changedTitle;
}
