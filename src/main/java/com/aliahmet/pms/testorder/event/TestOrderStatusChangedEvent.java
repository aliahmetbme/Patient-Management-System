package com.aliahmet.pms.testorder.event;

import com.aliahmet.pms.testorder.TestOrderStatus;
import com.aliahmet.pms.testorder.TestType;

import java.time.LocalDateTime;

public class TestOrderStatusChangedEvent {

    private final Long testOrderId;
    private final Long patientId;
    private final TestType testType;
    private final TestOrderStatus status;
    private final String resultMessage;
    private final LocalDateTime changedAt;

    public TestOrderStatusChangedEvent(
            Long testOrderId,
            Long patientId,
            TestType testType,
            TestOrderStatus status,
            String resultMessage
    ) {
        this.testOrderId = testOrderId;
        this.patientId = patientId;
        this.testType = testType;
        this.status = status;
        this.resultMessage = resultMessage;
        this.changedAt = LocalDateTime.now();
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