package com.aliahmet.pms.testorder.command;

import com.aliahmet.pms.department.LaboratoryDepartment;
import org.springframework.stereotype.Component;

@Component
public class EndocrinologyBloodTestCommand implements TestCommand {

    private final LaboratoryDepartment laboratoryDepartment;

    public EndocrinologyBloodTestCommand(
            LaboratoryDepartment laboratoryDepartment
    ) {
        this.laboratoryDepartment = laboratoryDepartment;
    }

    @Override
    public String execute() {
        return laboratoryDepartment.performEndocrinologyBloodTest();
    }
}