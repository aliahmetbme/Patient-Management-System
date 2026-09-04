package com.aliahmet.pms.clinic.form;

public interface ClinicFormFactory {

    InsuranceForm createInsuranceForm();

    MedicalHistoryForm createMedicalHistoryForm();

    ConsentForm createConsentForm();
}