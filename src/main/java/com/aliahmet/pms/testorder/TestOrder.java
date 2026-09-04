package com.aliahmet.pms.testorder;

import com.aliahmet.pms.patient.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_orders")
public class TestOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestOrderStatus status = TestOrderStatus.QUEUED;

    private  LocalDateTime completedAt;
    private String resultMessage;

    public TestOrder() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public TestOrderStatus getStatus() {
        return status;
    }

    public void setStatus(TestOrderStatus status) {
        this.status = status;
    }
}
