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
import com.aliahmet.pms.common.exception.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestOrderServiceTest {

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
        patient.setAdmissionReason("Patient admission was approved.");

        patient = patientRepository.save(patient);
    }

    @Test
    void testOrderShouldMoveFromQueuedToProcessingToCompleted() {

        CreateTestOrderRequest request =
                new CreateTestOrderRequest();

        request.setPatientId(patient.getId());
        request.setTestType(TestType.X_RAY);

        TestOrderResponse created =
                testOrderService.createTestOrder(request);

        assertEquals(
                TestOrderStatus.QUEUED,
                created.getStatus()
        );

        TestOrderResponse processing =
                testOrderService.prepareForExecution(
                        created.getId()
                );

        assertEquals(
                TestOrderStatus.PROCESSING,
                processing.getStatus()
        );

        testOrderService.executeTestOrder(
                created.getId()
        );

        TestOrderResponse completed =
                testOrderService.getTestOrderById(
                        created.getId()
                );

        assertEquals(
                TestOrderStatus.COMPLETED,
                completed.getStatus()
        );

        assertNotNull(
                completed.getCompletedAt()
        );

        assertEquals(
                "X-RAY was performed by Radiology Department",
                completed.getResultMessage()
        );
    }
    @Test
    void completedTestOrderShouldNotBeExecutedAgain() {

        CreateTestOrderRequest request =
                new CreateTestOrderRequest();

        request.setPatientId(patient.getId());
        request.setTestType(TestType.X_RAY);

        TestOrderResponse created =
                testOrderService.createTestOrder(request);

        testOrderService.prepareForExecution(
                created.getId()
        );

        testOrderService.executeTestOrder(
                created.getId()
        );

        InvalidTestOrderStateException exception =
                assertThrows(
                        InvalidTestOrderStateException.class,
                        () -> testOrderService.prepareForExecution(
                                created.getId()
                        )
                );

        assertEquals(
                "Only QUEUED test orders can be executed. Current status: COMPLETED",
                exception.getMessage()
        );
    }

    @Test
    void nonexistentTestOrderShouldThrowResourceNotFoundException() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> testOrderService.getTestOrderById(999L)
                );

        assertEquals(
                "Test order not found: 999",
                exception.getMessage()
        );
    }
    @Test
    void testOrderShouldNotBeCreatedForNonexistentPatient() {

        CreateTestOrderRequest request =
                new CreateTestOrderRequest();

        request.setPatientId(999L);
        request.setTestType(TestType.X_RAY);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> testOrderService.createTestOrder(request)
                );

        assertEquals(
                "Patient not found: 999",
                exception.getMessage()
        );
    }
}