package com.sadman.drs.protocol;

import com.sadman.drs.model.AssessmentResult;
import com.sadman.drs.model.ResponseTask;

import java.io.Serializable;
import java.util.List;

public class AssessmentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssessmentResult assessmentResult;
    private final List<ResponseTask> generatedTasks;

    public AssessmentResponse(AssessmentResult assessmentResult, List<ResponseTask> generatedTasks) {
        this.assessmentResult = assessmentResult;
        this.generatedTasks = generatedTasks;
    }

    public AssessmentResult getAssessmentResult() {
        return assessmentResult;
    }

    public List<ResponseTask> getGeneratedTasks() {
        return generatedTasks;
    }
}
