package com.aliahmet.pms.testorder.dto;

import com.aliahmet.pms.testorder.TestOrder;
import com.aliahmet.pms.testorder.TestType;
import jakarta.validation.constraints.NotNull;

public class CreateTestOrderRequest {

    @NotNull(message =  "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Test type is required")
    private TestType testType;

    public CreateTestOrderRequest () {}

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }
}
