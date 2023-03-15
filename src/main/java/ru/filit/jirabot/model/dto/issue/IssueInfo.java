package ru.filit.jirabot.model.dto.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInfo {
    @JsonProperty("code")
    private String code;
    @JsonProperty("status")
    private String status;
}
