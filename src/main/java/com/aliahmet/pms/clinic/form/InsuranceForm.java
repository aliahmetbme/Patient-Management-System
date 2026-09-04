package com.aliahmet.pms.clinic.form;

import com.aliahmet.pms.patient.InsuranceType;

public interface InsuranceForm {

    boolean isAccepted(InsuranceType insuranceType);
}