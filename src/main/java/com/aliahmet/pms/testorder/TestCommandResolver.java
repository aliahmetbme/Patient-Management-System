package com.aliahmet.pms.testorder;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class TestCommandResolver {

    private final Map<TestType, TestCommand> commands =
            new EnumMap<>(TestType.class);

    public TestCommandResolver(
            XRayCommand xRayCommand,
            EkgCommand ekgCommand,
            CardiologyBloodTestCommand cardiologyBloodTestCommand,
            EndocrinologyBloodTestCommand endocrinologyBloodTestCommand
    ) {
        commands.put(TestType.X_RAY, xRayCommand);
        commands.put(TestType.EKG, ekgCommand);
        commands.put(
                TestType.CARDIOLOGY_BLOOD_TEST,
                cardiologyBloodTestCommand
        );
        commands.put(
                TestType.ENDOCRINOLOGY_BLOOD_TEST,
                endocrinologyBloodTestCommand
        );
    }

    public TestCommand resolve(TestType testType) {
        TestCommand command = commands.get(testType);

        if (command == null) {
            throw new IllegalArgumentException(
                    "Unsupported test type: " + testType
            );
        }

        return command;
    }
}