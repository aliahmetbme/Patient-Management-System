package com.aliahmet.pms.testorder.dto;

import com.aliahmet.pms.testorder.TestOrderStatus;
import com.aliahmet.pms.testorder.TestType;

import java.time.LocalDateTime;

public class TestOrderResponse {

    private final Long id;
    private final Long patientId;
    private final String patientName;
    private final TestType testType;
    private final TestOrderStatus status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime completedAt;
    private final String resultMessage;

    public TestOrderResponse(
            Long id,
            Long patientId,
            String patientName,
            TestType testType,
            TestOrderStatus status,
            LocalDateTime requestedAt,
            LocalDateTime completedAt,
            String resultMessage
    ) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.testType = testType;
        this.status = status;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
        this.resultMessage = resultMessage;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public TestType getTestType() {
        return testType;
    }

    public TestOrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}