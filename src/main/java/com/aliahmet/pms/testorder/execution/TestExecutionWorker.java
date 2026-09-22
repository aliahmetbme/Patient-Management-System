package com.aliahmet.pms.testorder.execution;

import com.aliahmet.pms.testorder.TestOrderService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TestExecutionWorker {
    private final TestOrderService testOrderService;
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionWorker.class);

    public TestExecutionWorker (
            TestOrderService testOrderService
    ) {
        this.testOrderService = testOrderService;
    }

    @Async("testTaskExecutor")
    public void execute(Long testOrderId) {
        logger.info(
                "Test order " + testOrderId
                        + " is running on thread: "
                        + Thread.currentThread().getName()
        );

        testOrderService.executeTestOrder(testOrderId);
    }
}
