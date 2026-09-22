package com.aliahmet.pms.testorder.execution;

import com.aliahmet.pms.testorder.TestOrderService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
class TestExecutionWorkerConcurrencyTest {

    @Autowired
    private TestExecutionWorker testExecutionWorker;

    @MockitoBean
    private TestOrderService testOrderService;

    @Test
    void twoTestOrdersShouldRunOnDifferentWorkerThreads()
            throws Exception {

        CountDownLatch startedLatch =
                new CountDownLatch(2);

        CountDownLatch releaseLatch =
                new CountDownLatch(1);

        Set<String> threadNames =
                ConcurrentHashMap.newKeySet();

        doAnswer(invocation -> {

            String threadName =
                    Thread.currentThread().getName();

            threadNames.add(threadName);

            startedLatch.countDown();

            releaseLatch.await(
                    3,
                    TimeUnit.SECONDS
            );

            return null;

        }).when(testOrderService)
                .executeTestOrder(anyLong());

        try {

            testExecutionWorker.execute(1L);
            testExecutionWorker.execute(2L);

            boolean bothStarted =
                    startedLatch.await(
                            3,
                            TimeUnit.SECONDS
                    );

            assertTrue(
                    bothStarted,
                    "Both async tasks should start."
            );

            assertEquals(
                    2,
                    threadNames.size()
            );

            assertTrue(
                    threadNames.stream()
                            .allMatch(
                                    name ->
                                            name.startsWith(
                                                    "test-worker"
                                            )
                            )
            );

        } finally {

            releaseLatch.countDown();
        }
    }
}