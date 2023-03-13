package ru.filit.jirabot.model.dto.jira;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fields {
    @JsonProperty("customfield_17705")
    private String customfield_17705;
    @JsonProperty("attachment")
    private List<Attachment> attachment;
    @JsonProperty("customfield_17600")
    private String customfield_17600;
    @JsonProperty("customfield_13806")
    private String customfield_13806;
    @JsonProperty("created")
    private String created;
    @JsonProperty("workratio")
    private int workratio;
    @JsonProperty("aggregatetimespent")
    private int aggregatetimespent;
    @JsonProperty("timespent")
    private int timespent;
    @JsonProperty("creator")
    private Creator creator;
    @JsonProperty("aggregatetimeestimate")
    private int aggregatetimeestimate;
    @JsonProperty("customfield_13600")
    private String customfield_13600;
    @JsonProperty("status")
    private Status status;
/*    @JsonProperty("versions")
    private List<String> versions;*/
    @JsonProperty("timeestimate")
    private int timeestimate;
    @JsonProperty("customfield_12401")
    private String customfield_12401;
    @JsonProperty("priority")
    private Priority priority;
    @JsonProperty("customfield_12407")
    private String customfield_12407;
/*    @JsonProperty("fixVersions")
    private List<String> fixVersions;*/
    @JsonProperty("comment")
    private Comment comment;
    @JsonProperty("customfield_14601")
    private int customfield_14601;
    @JsonProperty("customfield_15019")
    private int customfield_15019;
    @JsonProperty("summary")
    private String summary;
    @JsonProperty("customfield_16904")
    private String customfield_16904;
    @JsonProperty("description")
    private String description;
    @JsonProperty("customfield_17325")
    private int customfield_17325;
    @JsonProperty("customfield_17326")
    private int customfield_17326;
/*    @JsonProperty("customfield_15020")
    private int customfield_15020;*/
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("resolutiondate")
    private String resolutiondate;
    @JsonProperty("project")
    private Project project;
    @JsonProperty("customfield_11009")
    private String customfield_11009;
    @JsonProperty("customfield_11006")
    private String customfield_11006;
    @JsonProperty("customfield_16700")
    private String customfield_16700;
    @JsonProperty("reporter")
    private Reporter reporter;
    /*@JsonProperty("customfield_14400")
    private String customfield_14400;
    @JsonProperty("subtasks")
    private List<String> subtasks;
    @JsonProperty("customfield_18217")
    private String customfield_18217;
    @JsonProperty("components")
    private List<String> components;
    @JsonProperty("assignee")
    private Assignee assignee;
    @JsonProperty("labels")
    private List<String> labels;*/
    @JsonProperty("customfield_16600")
    private String customfield_16600;
    @JsonProperty("lastViewed")
    private String lastViewed;
    @JsonProperty("customfield_14307")
    private String customfield_14307;
    @JsonProperty("resolution")
    private Resolution resolution;
    @JsonProperty("customfield_15405")
    private String customfield_15405;
    @JsonProperty("customfield_17703")
    private String customfield_17703;
}
