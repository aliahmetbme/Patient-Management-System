package com.aliahmet.pms.testorder.event.listener;

import com.aliahmet.pms.testorder.TestOrderStatus;
import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PatientNotificationListener {

    private static final Logger logger =  LoggerFactory.getLogger(PatientNotificationListener.class);

    @EventListener
    public void notifyPatient(
            TestOrderStatusChangedEvent event
    ) {
        if (event.getStatus() == TestOrderStatus.COMPLETED) {
            logger.info(
                    "PATIENT NOTIFICATION"
                            + " | patientId=" + event.getPatientId()
                            + " | testOrderId=" + event.getTestOrderId()
                            + " | message=Your test has been completed."
            );
        }

        if (event.getStatus() == TestOrderStatus.FAILED) {
            logger.info(
                    "PATIENT NOTIFICATION"
                            + " | patientId=" + event.getPatientId()
                            + " | testOrderId=" + event.getTestOrderId()
                            + " | message=Your test could not be completed."
            );
        }
    }
}