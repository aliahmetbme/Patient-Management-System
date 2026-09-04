package com.aliahmet.pms.patient.admission;

import com.aliahmet.pms.clinic.form.ClinicFormFactory;
import com.aliahmet.pms.clinic.form.ClinicFormFactoryResolver;
import com.aliahmet.pms.patient.AdmissionStatus;
import com.aliahmet.pms.patient.dto.PatientCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class PatientAdmissionFacade {

    private final ClinicFormFactoryResolver factoryResolver;

    public PatientAdmissionFacade(
            ClinicFormFactoryResolver factoryResolver
    ) {
        this.factoryResolver = factoryResolver;
    }

    public AdmissionResult evaluateAdmission(
            PatientCreateRequest request
    ) {
        ClinicFormFactory factory =
                factoryResolver.resolve(request.getClinicType());

        boolean insuranceAccepted =
                factory.createInsuranceForm()
                        .isAccepted(request.getInsuranceType());

        if (!insuranceAccepted) {
            return new AdmissionResult(
                    AdmissionStatus.REJECTED,
                    "Insurance type is not accepted by the selected clinic."
            );
        }

        boolean medicalHistoryValid =
                factory.createMedicalHistoryForm()
                        .isValid(request.getMedicalHistory());

        if (!medicalHistoryValid) {
            return new AdmissionResult(
                    AdmissionStatus.REJECTED,
                    "Medical history information is missing."
            );
        }

        boolean consentValid =
                factory.createConsentForm()
                        .isValid(Boolean.TRUE.equals(request.getConsentGiven()));

        if (!consentValid) {
            return new AdmissionResult(
                    AdmissionStatus.REJECTED,
                    "Patient consent has not been provided."
            );
        }

        return new AdmissionResult(
                AdmissionStatus.ADMITTED,
                "Patient admission was approved."
        );
    }
}