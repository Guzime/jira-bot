package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("created")
    private String created;
    @JsonProperty("body")
    private String body;
    @JsonProperty("author")
    private Author author;
    @JsonProperty("id")
    private String id;
    @JsonProperty("self")
    private String self;
}
