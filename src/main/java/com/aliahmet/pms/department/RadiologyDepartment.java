package com.aliahmet.pms.department;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RadiologyDepartment {
    public String performXRay() {
        return "X-RAY was performed by Radiology Department";
    }
    public String performEkg() {
        return "Ekg was performed by Radiology Department";
    }
}
