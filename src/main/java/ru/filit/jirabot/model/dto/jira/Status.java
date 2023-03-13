package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("iconUrl")
    private String iconUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("self")
    private String self;
}
