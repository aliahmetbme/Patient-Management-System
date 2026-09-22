package com.aliahmet.pms.patient;

import com.aliahmet.pms.testorder.TestOrderRepository;
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
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestOrderRepository testOrderRepository;

    @BeforeEach
    void setUp() {

        // Önce child tablo temizlenir.
        testOrderRepository.deleteAll();
        patientRepository.deleteAll();
    }


    // --------------------------------------------------
    // POST /api/patients
    // --------------------------------------------------

    @Test
    void validCardiologyPatientShouldReturn201AndBeAdmitted()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "Ayşe Kaya",
                  "email": "ayse.kaya@example.com",
                  "phone": "05551112233",
                  "clinicType": "CARDIOLOGY",
                  "insuranceType": "GOVERNMENT",
                  "medicalHistory": "Hypertension history",
                  "consentGiven": true
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patient.fullName")
                                .value("Ayşe Kaya")
                )
                .andExpect(
                        jsonPath("$.patient.clinicType")
                                .value("CARDIOLOGY")
                )
                .andExpect(
                        jsonPath("$.patient.admissionStatus")
                                .value("ADMITTED")
                )
                .andExpect(
                        jsonPath("$.patient.admissionReason")
                                .value("Patient admission was approved.")
                );
    }


    @Test
    void cardiologyPatientWithoutInsuranceShouldBeRejected()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "Mehmet Demir",
                  "email": "mehmet@example.com",
                  "phone": "05552223344",
                  "clinicType": "CARDIOLOGY",
                  "insuranceType": "NONE",
                  "medicalHistory": "Hypertension history",
                  "consentGiven": true
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patient.admissionStatus")
                                .value("REJECTED")
                );
    }


    @Test
    void orthopedicsPatientWithGovernmentInsuranceShouldBeRejected()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "Ali Yılmaz",
                  "email": "ali@example.com",
                  "phone": "05553334455",
                  "clinicType": "ORTHOPEDICS",
                  "insuranceType": "GOVERNMENT",
                  "medicalHistory": "Knee pain",
                  "consentGiven": true
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patient.admissionStatus")
                                .value("REJECTED")
                );
    }


    @Test
    void patientWithoutConsentShouldBeRejected()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "Zeynep Aydın",
                  "email": "zeynep@example.com",
                  "phone": "05554445566",
                  "clinicType": "CARDIOLOGY",
                  "insuranceType": "GOVERNMENT",
                  "medicalHistory": "Hypertension history",
                  "consentGiven": false
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.patient.admissionStatus")
                                .value("REJECTED")
                );
    }


    // --------------------------------------------------
    // VALIDATION
    // --------------------------------------------------

    @Test
    void patientWithoutFullNameShouldReturn400()
            throws Exception {

        String requestBody = """
                {
                  "email": "ayse@example.com",
                  "phone": "05551112233",
                  "clinicType": "CARDIOLOGY",
                  "insuranceType": "GOVERNMENT",
                  "medicalHistory": "History",
                  "consentGiven": true
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errors.fullName").value("Full name is required")
                );
    }


    @Test
    void patientWithInvalidEmailShouldReturn400()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "Ayşe Kaya",
                  "email": "invalid-email",
                  "phone": "05551112233",
                  "clinicType": "CARDIOLOGY",
                  "insuranceType": "GOVERNMENT",
                  "medicalHistory": "History",
                  "consentGiven": true
                }
                """;

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errors.email").value("Email format is invalid")
                );
    }


    // --------------------------------------------------
    // GET /api/patients
    // --------------------------------------------------

    @Test
    void getAllPatientsShouldReturn200()
            throws Exception {

        Patient patient = createPatient();

        mockMvc.perform(
                        get("/api/patients")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(patient.getId())
                )
                .andExpect(
                        jsonPath("$[0].fullName")
                                .value("Ayşe Kaya")
                )
                .andExpect(
                        jsonPath("$[0].admissionStatus")
                                .value("ADMITTED")
                );
    }


    // --------------------------------------------------
    // GET /api/patients/{id}
    // --------------------------------------------------

    @Test
    void getPatientByIdShouldReturn200()
            throws Exception {

        Patient patient = createPatient();

        mockMvc.perform(
                        get(
                                "/api/patients/{id}",
                                patient.getId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(patient.getId())
                )
                .andExpect(
                        jsonPath("$.fullName")
                                .value("Ayşe Kaya")
                )
                .andExpect(
                        jsonPath("$.clinicType")
                                .value("CARDIOLOGY")
                )
                .andExpect(
                        jsonPath("$.admissionStatus")
                                .value("ADMITTED")
                );
    }


    @Test
    void getNonexistentPatientShouldReturn404()
            throws Exception {

        mockMvc.perform(
                        get("/api/patients/999")
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
    // Helper
    // --------------------------------------------------

    private Patient createPatient() {

        Patient patient = new Patient();

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

        return patientRepository.save(patient);
    }
}