package com.aliahmet.pms.testorder;

import com.aliahmet.pms.exception.ApiErrorResponse;
import com.aliahmet.pms.exception.ValidationErrorResponse;
import com.aliahmet.pms.testorder.dto.CreateTestOrderRequest;
import com.aliahmet.pms.testorder.dto.TestOrderResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aliahmet.pms.testorder.execution.TestExecutionWorker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;


@Tag(
        name = "Test Orders",
        description = "Medical test ordering and execution operations"
)
@RestController
@RequestMapping("/api/test-orders")
public class TestOrderController {

    private final TestOrderService testOrderService;
    private final TestExecutionWorker testExecutionWorker;

    public TestOrderController(
            TestOrderService testOrderService,
            TestExecutionWorker testExecutionWorker
    ) {
        this.testOrderService = testOrderService;
        this.testExecutionWorker = testExecutionWorker;
    }
    @Operation(
            summary = "Create test order",
            description = "Creates a medical test order for an existing patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Test order created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid test order request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ValidationErrorResponse.class
                            )
                    )
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
    @PostMapping
    public ResponseEntity<TestOrderResponse> createTestOrder(
            @Valid @RequestBody CreateTestOrderRequest request
    ) {

        TestOrderResponse response =
                testOrderService.createTestOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all test orders",
            description = "Returns all medical test orders."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Test orders returned successfully"
    )
    @GetMapping
    public List<TestOrderResponse> getAllTestOrders() {
        return testOrderService.getAllTestOrders();
    }
    @Operation(
            summary = "Execute test order",
            description = "Moves a QUEUED test order to PROCESSING and starts asynchronous execution."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Test execution accepted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Test order is not in QUEUED state",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/execute")
    public ResponseEntity<TestOrderResponse> executeTestOrder(
            @PathVariable Long id
    ) {

        TestOrderResponse processingTestOrder =
                testOrderService.prepareForExecution(id);

        testExecutionWorker.execute(id);

        return ResponseEntity
                .accepted()
                .body(processingTestOrder);
    }

    @Operation(
            summary = "Get test order by ID",
            description = "Returns a single medical test order by database ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Test order found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test order not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TestOrderResponse> getTestOrderById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                testOrderService.getTestOrderById(id)
        );
    }
}
