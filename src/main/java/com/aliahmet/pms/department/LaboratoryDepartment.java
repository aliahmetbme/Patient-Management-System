package com.aliahmet.pms.department;

import org.springframework.stereotype.Service;

@Service
public class LaboratoryDepartment {

    public String performCardiologyBloodTest() {
        return "Cardiology blood test was performed by the Laboratory Department.";
    }

    public String performEndocrinologyBloodTest() {
        return "Endocrinology blood test was performed by the Laboratory Department.";
    }
}