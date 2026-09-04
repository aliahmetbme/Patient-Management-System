package com.aliahmet.pms.testorder.command;

import com.aliahmet.pms.department.RadiologyDepartment;
import org.springframework.stereotype.Component;

@Component
public class XRayCommand implements TestCommand {

    private final RadiologyDepartment radiologyDepartment;

    public XRayCommand(RadiologyDepartment radiologyDepartment) {
        this.radiologyDepartment = radiologyDepartment;
    }

    @Override
    public String execute() {
        return radiologyDepartment.performXRay();
    }
}