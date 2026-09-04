package com.aliahmet.pms.department;

import org.springframework.stereotype.Service;

@Service
public class RadiologyDepartment {
    public String performXRay() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "X-RAY operation was interrupted.",
                    exception
            );
        }

        return "X-RAY was performed by Radiology Department";
    }    public String performEkg() {
        return "Ekg was performed by Radiology Department";
    }
}
