package com.aliahmet.pms.testorder.websocket;

import com.aliahmet.pms.testorder.TestOrderStatus;
import com.aliahmet.pms.testorder.TestType;

import java.time.LocalDateTime;

public class TestOrderStatusMessage {

    private final Long testOrderId;
    private final Long patientId;
    private final TestType testType;
    private final TestOrderStatus status;
    private final String resultMessage;
    private final LocalDateTime changedAt;

    public TestOrderStatusMessage(
            Long testOrderId,
            Long patientId,
            TestType testType,
            TestOrderStatus status,
            String resultMessage,
            LocalDateTime changedAt
    ) {
        this.testOrderId = testOrderId;
        this.patientId = patientId;
        this.testType = testType;
        this.status = status;
        this.resultMessage = resultMessage;
        this.changedAt = changedAt;
    }

    public Long getTestOrderId() {
        return testOrderId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public TestType getTestType() {
        return testType;
    }

    public TestOrderStatus getStatus() {
        return status;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }
}