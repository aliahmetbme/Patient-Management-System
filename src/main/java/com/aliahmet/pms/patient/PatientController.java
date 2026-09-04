package com.aliahmet.pms.patient;

import com.aliahmet.pms.patient.dto.PatientCreateResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.aliahmet.pms.patient.dto.PatientCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientCreateResponse> createPatient(
            @Valid @RequestBody PatientCreateRequest request
    ) {
        PatientCreateResponse createdPatient = patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPatient);
    }

    @GetMapping
    public List<Patient> getAllPatient() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById (@PathVariable Long id) {
        return patientService.getPatientById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
