package com.aliahmet.pms.testorder.execution;

import com.aliahmet.pms.testorder.TestOrderService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestExecutionWorker {
    private final TestOrderService testOrderService;

    public TestExecutionWorker (
            TestOrderService testOrderService
    ) {
        this.testOrderService = testOrderService;
    }

    @Async("testTaskExecutor")
    public void execute(Long testOrderId) {
        System.out.println(
                "Test order " + testOrderId
                        + " is running on thread: "
                        + Thread.currentThread().getName()
        );

        testOrderService.executeTestOrder(testOrderId);
    }
}
