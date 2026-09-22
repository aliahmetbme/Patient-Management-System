package com.aliahmet.pms;

import com.aliahmet.pms.patient.AdmissionStatus;
import com.aliahmet.pms.patient.ClinicType;
import com.aliahmet.pms.patient.InsuranceType;
import com.aliahmet.pms.patient.admission.AdmissionResult;
import com.aliahmet.pms.patient.admission.PatientAdmissionFacade;
import com.aliahmet.pms.patient.dto.PatientCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PatientAdmissionFacadeTest {

    @Autowired
    private PatientAdmissionFacade patientAdmissionFacade;

    @Test
    void validCardiologyPatientShouldBeAdmitted() {

        PatientCreateRequest request = new PatientCreateRequest();

        request.setFullName("Ayşe Kaya");
        request.setEmail("ayse.kaya@example.com");
        request.setPhone("05551112233");
        request.setClinicType(ClinicType.CARDIOLOGY);
        request.setInsuranceType(InsuranceType.GOVERNMENT);
        request.setMedicalHistory("Hypertension history");
        request.setConsentGiven(true);

        AdmissionResult result =
                patientAdmissionFacade.evaluateAdmission(request);

        assertEquals(
                AdmissionStatus.ADMITTED,
                result.getStatus()
        );

        assertEquals(
                "Patient admission was approved.",
                result.getReason()
        );
    }

    @Test
    void cardiologyPatientWithoutInsuranceShouldBeRejected() {

        PatientCreateRequest request = new PatientCreateRequest();

        request.setFullName("Mehmet Demir");
        request.setEmail("mehmet.demir@example.com");
        request.setPhone("05552223344");
        request.setClinicType(ClinicType.CARDIOLOGY);
        request.setInsuranceType(InsuranceType.NONE);
        request.setMedicalHistory("Hypertension history");
        request.setConsentGiven(true);

        AdmissionResult result =
                patientAdmissionFacade.evaluateAdmission(request);

        assertEquals(
                AdmissionStatus.REJECTED,
                result.getStatus()
        );
    }

    @Test
    void orthopedicsPatientWithGovernmentInsuranceShouldBeRejected() {

        PatientCreateRequest request = new PatientCreateRequest();

        request.setFullName("Ali Yılmaz");
        request.setEmail("ali.yilmaz@example.com");
        request.setPhone("05553334455");
        request.setClinicType(ClinicType.ORTHOPEDICS);
        request.setInsuranceType(InsuranceType.GOVERNMENT);
        request.setMedicalHistory("Knee pain");
        request.setConsentGiven(true);

        AdmissionResult result =
                patientAdmissionFacade.evaluateAdmission(request);

        assertEquals(
                AdmissionStatus.REJECTED,
                result.getStatus()
        );
    }

    @Test
    void patientWithoutConsentShouldBeRejected() {

        PatientCreateRequest request = new PatientCreateRequest();

        request.setFullName("Zeynep Aydın");
        request.setEmail("zeynep.aydin@example.com");
        request.setPhone("05554445566");
        request.setClinicType(ClinicType.CARDIOLOGY);
        request.setInsuranceType(InsuranceType.GOVERNMENT);
        request.setMedicalHistory("Hypertension history");
        request.setConsentGiven(false);

        AdmissionResult result =
                patientAdmissionFacade.evaluateAdmission(request);

        assertEquals(
                AdmissionStatus.REJECTED,
                result.getStatus()
        );
    }


}