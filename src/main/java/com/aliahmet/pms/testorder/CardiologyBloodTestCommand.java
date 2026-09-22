package com.aliahmet.pms.testorder;

import com.aliahmet.pms.department.LaboratoryDepartment;
import org.springframework.stereotype.Component;

@Component
public class CardiologyBloodTestCommand implements TestCommand {

    private final LaboratoryDepartment laboratoryDepartment;

    public CardiologyBloodTestCommand(
            LaboratoryDepartment laboratoryDepartment
    ) {
        this.laboratoryDepartment = laboratoryDepartment;
    }

    @Override
    public String execute() {
        return laboratoryDepartment.performCardiologyBloodTest();
    }
}