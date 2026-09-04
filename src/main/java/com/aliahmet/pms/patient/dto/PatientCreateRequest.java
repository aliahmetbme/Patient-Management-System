package com.aliahmet.pms.patient.dto;

import com.aliahmet.pms.patient.ClinicType;
import com.aliahmet.pms.patient.InsuranceType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class PatientCreateRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Clinic type is required")
    private ClinicType clinicType;

    @NotNull(message = "Insurance type is required")
    private InsuranceType insuranceType;

    @NotBlank(message = "Medical history is required")
    private String medicalHistory;

    @NotNull(message = "Consent information is required")
    private Boolean consentGiven;

    public PatientCreateRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ClinicType getClinicType() {
        return clinicType;
    }

    public void setClinicType(ClinicType clinicType) {
        this.clinicType = clinicType;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public Boolean getConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}
