package com.aliahmet.pms.clinic.form;

import com.aliahmet.pms.patient.InsuranceType;
import org.springframework.stereotype.Component;

@Component
public class OrthopedicsFormFactory implements ClinicFormFactory {

    @Override
    public InsuranceForm createInsuranceForm() {
        return insuranceType ->
                insuranceType == InsuranceType.PRIVATE;
    }

    @Override
    public MedicalHistoryForm createMedicalHistoryForm() {
        return medicalHistory ->
                medicalHistory != null
                        && !medicalHistory.isBlank();
    }

    @Override
    public ConsentForm createConsentForm() {
        return consentGiven -> consentGiven;
    }
}