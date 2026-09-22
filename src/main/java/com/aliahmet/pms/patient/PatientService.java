package com.aliahmet.pms.patient;

import com.aliahmet.pms.common.exception.ResourceNotFoundException;
import com.aliahmet.pms.patient.admission.AdmissionResult;
import org.springframework.stereotype.Service;

import com.aliahmet.pms.patient.dto.PatientCreateRequest;
import com.aliahmet.pms.patient.dto.PatientCreateResponse;
import com.aliahmet.pms.patient.admission.PatientAdmissionFacade;

import java.util.List;
import java.util.Optional;
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientAdmissionFacade patientAdmissionFacade;

    public PatientService(
            PatientRepository patientRepository,
            PatientAdmissionFacade patientAdmissionFacade
    ) {
        this.patientRepository = patientRepository;
        this.patientAdmissionFacade = patientAdmissionFacade;
    }

    public PatientCreateResponse createPatient(PatientCreateRequest request) {
        Patient patient = new Patient();

        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setClinicType(request.getClinicType());
        patient.setInsuranceType(request.getInsuranceType());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setConsentGiven(Boolean.TRUE.equals(request.getConsentGiven()));

        AdmissionResult admissionResult =
                patientAdmissionFacade.evaluateAdmission(request);

        patient.setAdmissionStatus(admissionResult.getStatus());
        patient.setAdmissionReason(admissionResult.getReason());
        patient = patientRepository.save(patient);

        return new PatientCreateResponse(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long patientId) {

        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found: " + patientId
                ));
    }
}
