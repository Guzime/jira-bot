package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Priority {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("iconUrl")
    private String iconUrl;
    @JsonProperty("self")
    private String self;
}
