package com.aliahmet.pms.testorder;

import com.aliahmet.pms.common.exception.InvalidTestOrderStateException;
import com.aliahmet.pms.common.exception.ResourceNotFoundException;
import com.aliahmet.pms.patient.Patient;
import com.aliahmet.pms.patient.PatientRepository;
import com.aliahmet.pms.testorder.dto.CreateTestOrderRequest;

import org.springframework.stereotype.Service;

import com.aliahmet.pms.testorder.dto.TestOrderResponse;
import org.springframework.transaction.annotation.Transactional;

import com.aliahmet.pms.testorder.command.TestCommandInvoker;

import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TestOrderService {

    private final TestOrderRepository testOrderRepository;
    private final PatientRepository patientRepository;
    private final TestCommandInvoker testCommandInvoker;
    private final ApplicationEventPublisher eventPublisher;

    public TestOrderService(
            TestOrderRepository testOrderRepository,
            PatientRepository patientRepository,
            TestCommandInvoker testCommandInvoker,
            ApplicationEventPublisher eventPublisher
    ) {
        this.testOrderRepository = testOrderRepository;
        this.patientRepository = patientRepository;
        this.testCommandInvoker = testCommandInvoker;
        this.eventPublisher = eventPublisher;
    }

    public TestOrderResponse createTestOrder (CreateTestOrderRequest request) {

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found: " + request.getPatientId()
                ));


        TestOrder testOrder = new TestOrder();

        testOrder.setPatient(patient);
        testOrder.setTestType(request.getTestType());
        testOrder.setStatus(TestOrderStatus.QUEUED);

        TestOrder savedTestOrder =  testOrderRepository.save(testOrder);

        return toResponse(savedTestOrder);
    }

    @Transactional(readOnly = true)
    public List<TestOrderResponse> getAllTestOrders() {
        return testOrderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void executeTestOrder(Long testOrderId) {

        TestOrder testOrder = testOrderRepository
                .findById(testOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Test order not found: " + testOrderId
                ));

        if (testOrder.getStatus() != TestOrderStatus.PROCESSING) {
            throw new InvalidTestOrderStateException(
                    "Only QUEUED test orders can be executed. Current status: "
                            + testOrder.getStatus()
            );
        }

        try {
            String resultMessage =
                    testCommandInvoker.execute(testOrder.getTestType());

            testOrder.setResultMessage(resultMessage);
            testOrder.setStatus(TestOrderStatus.COMPLETED);
            testOrder.setCompletedAt(LocalDateTime.now());

        } catch (RuntimeException exception) {

            testOrder.setStatus(TestOrderStatus.FAILED);
            testOrder.setResultMessage(exception.getMessage());
            testOrder.setCompletedAt(LocalDateTime.now());
        }

        TestOrder savedTestOrder =  testOrderRepository.save(testOrder);
        publishStatusChangedEvent(savedTestOrder);

    }

    private TestOrderResponse toResponse(
            TestOrder testOrder
    ) {
        Patient patient = testOrder.getPatient();

        return new TestOrderResponse(
                testOrder.getId(),
                patient.getId(),
                patient.getFullName(),
                testOrder.getTestType(),
                testOrder.getStatus(),
                testOrder.getRequestedAt(),
                testOrder.getCompletedAt(),
                testOrder.getResultMessage()
        );
    }

    private void publishStatusChangedEvent(TestOrder testOrder) {

        TestOrderStatusChangedEvent event =
                new TestOrderStatusChangedEvent(
                        testOrder.getId(),
                        testOrder.getPatient().getId(),
                        testOrder.getTestType(),
                        testOrder.getStatus(),
                        testOrder.getResultMessage()
                );

        eventPublisher.publishEvent(event);
    }

    @Transactional(readOnly = true)
    public TestOrderResponse getTestOrderById (Long testOrderId) {

        TestOrder testOrder = testOrderRepository.findById(testOrderId).orElseThrow(() -> new IllegalArgumentException(
                "Test order not found: " + testOrderId
        ));

        return toResponse(testOrder);
    }

    @Transactional
    public TestOrderResponse prepareForExecution(Long testOrderId) {

        TestOrder testOrder = testOrderRepository
                .findByIdForUpdate(testOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Test order not found: " + testOrderId
                ));

        if (testOrder.getStatus() != TestOrderStatus.QUEUED) {
            throw new InvalidTestOrderStateException(
                    "Only QUEUED test orders can be executed. Current status: "
                            + testOrder.getStatus()
            );
        }

        testOrder.setStatus(TestOrderStatus.PROCESSING);

        TestOrder savedTestOrder =
                testOrderRepository.save(testOrder);

        publishStatusChangedEvent(savedTestOrder);

        return toResponse(savedTestOrder);
    }
}
