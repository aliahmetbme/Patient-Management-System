package com.aliahmet.pms.testorder.event.listener;

import com.aliahmet.pms.testorder.TestOrderStatus;
import com.aliahmet.pms.testorder.event.TestOrderStatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PatientNotificationListener {

    @EventListener
    public void notifyPatient(
            TestOrderStatusChangedEvent event
    ) {
        if (event.getStatus() == TestOrderStatus.COMPLETED) {
            System.out.println(
                    "PATIENT NOTIFICATION"
                            + " | patientId=" + event.getPatientId()
                            + " | testOrderId=" + event.getTestOrderId()
                            + " | message=Your test has been completed."
            );
        }

        if (event.getStatus() == TestOrderStatus.FAILED) {
            System.out.println(
                    "PATIENT NOTIFICATION"
                            + " | patientId=" + event.getPatientId()
                            + " | testOrderId=" + event.getTestOrderId()
                            + " | message=Your test could not be completed."
            );
        }
    }
}