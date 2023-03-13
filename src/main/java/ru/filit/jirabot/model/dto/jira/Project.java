package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @JsonProperty("projectTypeKey")
    private String projectTypeKey;
    @JsonProperty("name")
    private String name;
    @JsonProperty("key")
    private String key;
    @JsonProperty("id")
    private String id;
    @JsonProperty("self")
    private String self;
}