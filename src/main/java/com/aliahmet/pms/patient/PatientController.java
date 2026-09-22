package com.aliahmet.pms.patient;

import com.aliahmet.pms.exception.ApiErrorResponse;
import com.aliahmet.pms.exception.ValidationErrorResponse;
import com.aliahmet.pms.patient.dto.PatientCreateResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

import com.aliahmet.pms.patient.dto.PatientCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Patients",
        description = "Patient registration and admission operations"
)
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(
            summary = "Create patient",
            description = "Registers a patient and evaluates admission rules."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Patient created successfully"

            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid patient data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ValidationErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<PatientCreateResponse> createPatient(
            @Valid @RequestBody PatientCreateRequest request
    ) {
        PatientCreateResponse createdPatient = patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPatient);
    }

    @Operation(
            summary = "Get all patients",
            description = "Returns all registered patients."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Patients returned successfully"

    )
    @GetMapping    public List<Patient> getAllPatient() {
        return patientService.getAllPatients();
    }

    @Operation(
            summary = "Get patient by ID",
            description = "Returns a single patient by database ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById (@PathVariable Long id) {
        return ResponseEntity.ok(
                patientService.getPatientById(id)
        );
    }


}
