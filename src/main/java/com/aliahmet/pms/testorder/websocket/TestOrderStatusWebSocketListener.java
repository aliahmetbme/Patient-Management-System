package com.aliahmet.pms.testorder.websocket;

import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
@Component
public class TestOrderStatusWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    public TestOrderStatusWebSocketListener (SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleStatusChanged (TestOrderStatusChangedEvent event) {
        TestOrderStatusMessage message = new TestOrderStatusMessage(
                event.getTestOrderId(),
                event.getPatientId(),
                event.getTestType(),
                event.getStatus(),
                event.getResultMessage(),
                event.getChangedAt()
        );

        messagingTemplate.convertAndSend(
                "/topic/test-orders",
                message
        );
    }
}
