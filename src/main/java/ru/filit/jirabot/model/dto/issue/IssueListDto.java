package ru.filit.jirabot.model.dto.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.filit.jirabot.model.dto.ResponseResult;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class IssueListDto {
    @JsonProperty("data")
    private List<IssueInfo> data;
    @JsonProperty("result")
    private ResponseResult result;
}
