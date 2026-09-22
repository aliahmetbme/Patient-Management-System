package com.aliahmet.pms.testorder;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.when;

@SpringBootTest
class TestOrderFailureTest {

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestOrderRepository testOrderRepository;

    @MockitoBean
    private TestCommandInvoker testCommandInvoker;

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
    void testOrderShouldBecomeFailedWhenCommandThrowsException() {

        when(
                testCommandInvoker.execute(TestType.X_RAY)
        ).thenThrow(
                new RuntimeException("Radiology device unavailable")
        );

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

        TestOrderResponse failed =
                testOrderService.getTestOrderById(
                        created.getId()
                );

        assertEquals(
                TestOrderStatus.FAILED,
                failed.getStatus()
        );

        assertEquals(
                "Radiology device unavailable",
                failed.getResultMessage()
        );

        assertNotNull(
                failed.getCompletedAt()
        );
    }
}