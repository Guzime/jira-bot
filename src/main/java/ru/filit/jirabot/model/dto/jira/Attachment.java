package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("content")
    private String content;
    @JsonProperty("mimeType")
    private String mimeType;
    @JsonProperty("size")
    private int size;
    @JsonProperty("created")
    private String created;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("id")
    private String id;
    @JsonProperty("self")
    private String self;
}
