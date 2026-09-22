package com.aliahmet.pms.testorder.event.listener;

import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TestOrderStatusLogListener {

    private static final Logger logger = LoggerFactory.getLogger(TestOrderStatusLogListener.class);

    @EventListener
    public void handleStatusChanged(
            TestOrderStatusChangedEvent event
    ) {
        logger.info(
                "TEST STATUS EVENT"
                        + " | testOrderId=" + event.getTestOrderId()
                        + " | patientId=" + event.getPatientId()
                        + " | testType=" + event.getTestType()
                        + " | status=" + event.getStatus()
                        + " | result=" + event.getResultMessage()
                        + " | changedAt=" + event.getChangedAt()
                        + " | thread=" + Thread.currentThread().getName()
        );
    }
}