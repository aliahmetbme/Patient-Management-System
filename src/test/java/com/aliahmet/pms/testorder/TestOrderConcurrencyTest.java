package com.aliahmet.pms.testorder;

import com.aliahmet.pms.common.exception.InvalidTestOrderStateException;
import com.aliahmet.pms.patient.AdmissionStatus;
import com.aliahmet.pms.patient.ClinicType;
import com.aliahmet.pms.patient.InsuranceType;
import com.aliahmet.pms.patient.Patient;
import com.aliahmet.pms.patient.PatientRepository;
import com.aliahmet.pms.testorder.dto.CreateTestOrderRequest;
import com.aliahmet.pms.testorder.dto.TestOrderResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestOrderConcurrencyTest {

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Patient patient;

    @BeforeEach
    void setUp() {

        testOrderRepository.deleteAll();
        patientRepository.deleteAll();

        patient = new Patient();

        patient.setFullName("Ayşe Kaya");
        patient.setEmail("ayse.kaya@example.com");
        patient.setPhone("05551112233");
        patient.setClinicType(ClinicType.CARDIOLOGY);
        patient.setInsuranceType(InsuranceType.GOVERNMENT);
        patient.setMedicalHistory("Hypertension history");
        patient.setConsentGiven(true);
        patient.setAdmissionStatus(AdmissionStatus.ADMITTED);
        patient.setAdmissionReason(
                "Patient admission was approved."
        );

        patient = patientRepository.save(patient);
    }

    @Test
    void onlyOneConcurrentExecutionRequestShouldSucceed()
            throws Exception {

        CreateTestOrderRequest request =
                new CreateTestOrderRequest();

        request.setPatientId(patient.getId());
        request.setTestType(TestType.X_RAY);

        TestOrderResponse created =
                testOrderService.createTestOrder(request);

        Long testOrderId = created.getId();

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startGate =
                new CountDownLatch(1);

        Future<Object> first =
                executor.submit(() -> {

                    startGate.await();

                    try {
                        return testOrderService
                                .prepareForExecution(
                                        testOrderId
                                );
                    } catch (Exception exception) {
                        return exception;
                    }
                });

        Future<Object> second =
                executor.submit(() -> {

                    startGate.await();

                    try {
                        return testOrderService
                                .prepareForExecution(
                                        testOrderId
                                );
                    } catch (Exception exception) {
                        return exception;
                    }
                });

        startGate.countDown();

        Object firstResult = first.get();
        Object secondResult = second.get();

        executor.shutdown();

        int successCount = 0;
        int conflictCount = 0;

        if (firstResult instanceof TestOrderResponse) {
            successCount++;
        }

        if (secondResult instanceof TestOrderResponse) {
            successCount++;
        }

        if (firstResult instanceof InvalidTestOrderStateException) {
            conflictCount++;
        }

        if (secondResult instanceof InvalidTestOrderStateException) {
            conflictCount++;
        }

        assertEquals(1, successCount);
        assertEquals(1, conflictCount);

        TestOrderResponse finalState =
                testOrderService.getTestOrderById(
                        testOrderId
                );

        assertEquals(
                TestOrderStatus.PROCESSING,
                finalState.getStatus()
        );
    }
}