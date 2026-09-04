package com.aliahmet.pms.testorder.event.listener;

import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestOrderStatusLogListener {

    @EventListener
    public void handleStatusChanged(
            TestOrderStatusChangedEvent event
    ) {
        System.out.println(
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