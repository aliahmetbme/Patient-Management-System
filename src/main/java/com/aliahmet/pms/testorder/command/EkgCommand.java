package com.aliahmet.pms.testorder.command;

import com.aliahmet.pms.department.RadiologyDepartment;
import org.springframework.stereotype.Component;

@Component
public class EkgCommand implements TestCommand {

    private final RadiologyDepartment radiologyDepartment;

    public EkgCommand(RadiologyDepartment radiologyDepartment) {
        this.radiologyDepartment = radiologyDepartment;
    }

    @Override
    public String execute() {
        return radiologyDepartment.performEkg();
    }
}