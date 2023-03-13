package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @JsonProperty("startAt")
    private int startAt;
    @JsonProperty("total")
    private int total;
    @JsonProperty("maxResults")
    private int maxResults;
    @JsonProperty("comments")
    private List<Comments> comments;
}