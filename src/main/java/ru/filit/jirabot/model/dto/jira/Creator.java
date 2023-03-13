package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creator {
    @JsonProperty("timeZone")
    private String timeZone;
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("key")
    private String key;
    @JsonProperty("name")
    private String name;
    @JsonProperty("self")
    private String self;
}
