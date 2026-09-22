package com.aliahmet.pms.testorder;

import com.aliahmet.pms.patient.AdmissionStatus;
import com.aliahmet.pms.patient.ClinicType;
import com.aliahmet.pms.patient.InsuranceType;
import com.aliahmet.pms.patient.Patient;
import com.aliahmet.pms.patient.PatientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TestOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestOrderRepository testOrderRepository;

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


    // --------------------------------------------------
    // POST /api/test-orders
    // --------------------------------------------------

    @Test
    void createTestOrderShouldReturn201() throws Exception {

        String requestBody = """
                {
                  "patientId": %d,
                  "testType": "X_RAY"
                }
                """.formatted(patient.getId());

        mockMvc.perform(
                        post("/api/test-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patientId")
                                .value(patient.getId())
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ayşe Kaya")
                )
                .andExpect(
                        jsonPath("$.testType")
                                .value("X_RAY")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("QUEUED")
                );
    }


    @Test
    void createTestOrderWithoutTestTypeShouldReturn400()
            throws Exception {

        String requestBody = """
                {
                  "patientId": %d
                }
                """.formatted(patient.getId());

        mockMvc.perform(
                        post("/api/test-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errors.testType")
                                .value("Test type is required")
                );
    }


    @Test
    void createTestOrderWithoutPatientIdShouldReturn400()
            throws Exception {

        String requestBody = """
                {
                  "testType": "X_RAY"
                }
                """;

        mockMvc.perform(
                        post("/api/test-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void createTestOrderForNonexistentPatientShouldReturn404()
            throws Exception {

        String requestBody = """
                {
                  "patientId": 999,
                  "testType": "X_RAY"
                }
                """;

        mockMvc.perform(
                        post("/api/test-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.error")
                                .value("Resource not found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Patient not found: 999")
                );
    }


    // --------------------------------------------------
    // GET /api/test-orders
    // --------------------------------------------------

    @Test
    void getAllTestOrdersShouldReturn200() throws Exception {

        TestOrder testOrder = createQueuedTestOrder();

        mockMvc.perform(
                        get("/api/test-orders")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(testOrder.getId())
                )
                .andExpect(
                        jsonPath("$[0].patientId")
                                .value(patient.getId())
                )
                .andExpect(
                        jsonPath("$[0].testType")
                                .value("X_RAY")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("QUEUED")
                );
    }


    // --------------------------------------------------
    // GET /api/test-orders/{id}
    // --------------------------------------------------

    @Test
    void getTestOrderByIdShouldReturn200() throws Exception {

        TestOrder testOrder = createQueuedTestOrder();

        mockMvc.perform(
                        get(
                                "/api/test-orders/{id}",
                                testOrder.getId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(testOrder.getId())
                )
                .andExpect(
                        jsonPath("$.patientId")
                                .value(patient.getId())
                )
                .andExpect(
                        jsonPath("$.patientName")
                                .value("Ayşe Kaya")
                )
                .andExpect(
                        jsonPath("$.testType")
                                .value("X_RAY")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("QUEUED")
                );
    }


    @Test
    void getNonexistentTestOrderShouldReturn404()
            throws Exception {

        mockMvc.perform(
                        get("/api/test-orders/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.error")
                                .value("Resource not found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Test order not found: 999")
                );
    }


    // --------------------------------------------------
    // POST /api/test-orders/{id}/execute
    // --------------------------------------------------

    @Test
    void executeQueuedTestOrderShouldReturn202()
            throws Exception {

        TestOrder testOrder = createQueuedTestOrder();

        mockMvc.perform(
                        post(
                                "/api/test-orders/{id}/execute",
                                testOrder.getId()
                        )
                )
                .andExpect(status().isAccepted())
                .andExpect(
                        jsonPath("$.id")
                                .value(testOrder.getId())
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PROCESSING")
                );
    }


    @Test
    void executeNonexistentTestOrderShouldReturn404()
            throws Exception {

        mockMvc.perform(
                        post("/api/test-orders/999/execute")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.error")
                                .value("Resource not found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Test order not found: 999")
                );
    }


    @Test
    void executeCompletedTestOrderShouldReturn409()
            throws Exception {

        TestOrder testOrder = new TestOrder();

        testOrder.setPatient(patient);
        testOrder.setTestType(TestType.X_RAY);
        testOrder.setStatus(TestOrderStatus.COMPLETED);

        testOrder = testOrderRepository.save(testOrder);

        mockMvc.perform(
                        post(
                                "/api/test-orders/{id}/execute",
                                testOrder.getId()
                        )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.error")
                                .value("Test order state conflict")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Only QUEUED test orders can be executed. Current status: COMPLETED"
                                )
                );
    }


    // --------------------------------------------------
    // Helper
    // --------------------------------------------------

    private TestOrder createQueuedTestOrder() {

        TestOrder testOrder = new TestOrder();

        testOrder.setPatient(patient);
        testOrder.setTestType(TestType.X_RAY);
        testOrder.setStatus(TestOrderStatus.QUEUED);

        return testOrderRepository.save(testOrder);
    }
}