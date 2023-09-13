package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain.EXAM;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamViewData {

    @JsonProperty(EXAM.ATTR_UUID)
    public final String uuid;

    @JsonProperty(EXAM.ATTR_NAME)
    public final String name;


    @JsonCreator
    public ExamViewData(
        @JsonProperty(EXAM.ATTR_UUID) final String uuid,
        @JsonProperty(EXAM.ATTR_NAME) final String name
    ){
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}