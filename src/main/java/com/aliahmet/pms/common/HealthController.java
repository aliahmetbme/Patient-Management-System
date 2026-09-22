package com.aliahmet.pms.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "System",
        description = "Application health and system status operations"
)
@RestController
@RequestMapping("/api")
public class HealthController {

    @Operation(
            summary = "Check application health",
            description = "Checks whether the Patient Management System backend is running."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Application is running"
    )
    @GetMapping("/health")
    public String health() {
        return "Patient Management System is running";
    }
}
