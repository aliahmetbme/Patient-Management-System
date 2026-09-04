package com.aliahmet.pms.clinic.form;

import com.aliahmet.pms.patient.ClinicType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
@Component
public class ClinicFormFactoryResolver {

    private final Map<ClinicType, ClinicFormFactory> factories;

    public ClinicFormFactoryResolver(
            CardiologyFormFactory cardiologyFormFactory,
            OrthopedicsFormFactory orthopedicsFormFactory,
            EndocrinologyFormFactory endocrinologyFormFactory
    ) {
        factories = new EnumMap<>(ClinicType.class);

        factories.put(
                ClinicType.CARDIOLOGY,
                cardiologyFormFactory
        );

        factories.put(
                ClinicType.ORTHOPEDICS,
                orthopedicsFormFactory
        );

        factories.put(
                ClinicType.ENDOCRINOLOGY,
                endocrinologyFormFactory
        );
    }

    public ClinicFormFactory resolve(ClinicType clinicType) {
        ClinicFormFactory factory = factories.get(clinicType);

        if (factory == null) {
            throw new IllegalArgumentException(
                    "Unsupported clinic type: " + clinicType
            );
        }

        return factory;
    }
}