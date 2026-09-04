package com.aliahmet.pms.patient.admission;

import com.aliahmet.pms.patient.AdmissionStatus;

public class AdmissionResult {

    private final AdmissionStatus status;
    private final String reason;

    public AdmissionResult(
            AdmissionStatus status,
            String reason
    ) {
        this.status = status;
        this.reason = reason;
    }

    public AdmissionStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

}
