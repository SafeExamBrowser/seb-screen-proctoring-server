package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain.EXAM;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamViewData {

    public static final String ATTR_IS_RUNNING = "isRunning";

    public static final ExamViewData EMPTY_MODEL = new ExamViewData(null, null, null, null, null);

    @JsonProperty(EXAM.ATTR_UUID)
    public final String uuid;

    @JsonProperty(EXAM.ATTR_NAME)
    public final String name;

    // TODO Use termination_time here instead of isRunning
    //      If an Exam should be shown to the user (isRunning) depends then on user view (roles and privileges)
    @JsonProperty(ATTR_IS_RUNNING)
    public final Boolean isRunning;

    @JsonProperty(EXAM.ATTR_START_TIME)
    public final Long startTime;

    @JsonProperty(EXAM.ATTR_END_TIME)
    public final Long endTime;

    @JsonCreator
    public ExamViewData(
        @JsonProperty(EXAM.ATTR_UUID) final String uuid,
        @JsonProperty(EXAM.ATTR_NAME) final String name,
        @JsonProperty(ATTR_IS_RUNNING) final Boolean isRunning,
        @JsonProperty(EXAM.ATTR_START_TIME) final Long startTime,
        @JsonProperty(EXAM.ATTR_END_TIME) final Long endTime
    ){
        this.uuid = uuid;
        this.name = name;
        this.isRunning = isRunning;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}