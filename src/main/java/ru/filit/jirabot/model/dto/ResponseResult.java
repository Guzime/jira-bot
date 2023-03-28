package ru.filit.jirabot.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.filit.jirabot.model.type.StatusCode;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC")
    private Date timestamp;
    @JsonProperty("status")
    private int status;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("id")
    private String id;

    public ResponseResult(int status, StatusCode code) {
        this.timestamp = new Date();
        this.status = status;
        this.code = code.getCode();
        this.id = UUID.randomUUID().toString();
        this.message = code.getMessage();
    }

}
