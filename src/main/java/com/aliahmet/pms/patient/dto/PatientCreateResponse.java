package com.aliahmet.pms.patient.dto;

import com.aliahmet.pms.patient.Patient;

public class PatientCreateResponse {

    private final Patient patient;

    public PatientCreateResponse(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }
}