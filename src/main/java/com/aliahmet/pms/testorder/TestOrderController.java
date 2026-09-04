package com.aliahmet.pms.testorder;

import com.aliahmet.pms.testorder.dto.CreateTestOrderRequest;
import com.aliahmet.pms.testorder.dto.TestOrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aliahmet.pms.testorder.execution.TestExecutionWorker;

import java.util.List;


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
    @PostMapping
    public ResponseEntity<TestOrderResponse> createTestOrder (
            @Valid @RequestBody CreateTestOrderRequest request
    ) {
        TestOrderResponse createdTestOrder = testOrderService.createTestOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestOrder);
    }

    @GetMapping
    public List<TestOrderResponse> getAllTestOrders() {
        return testOrderService.getAllTestOrders();
    }

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
}
