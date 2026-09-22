package com.aliahmet.pms.testorder;

import org.springframework.stereotype.Component;

@Component
public class TestCommandInvoker {

    private final TestCommandResolver testCommandResolver;

    public TestCommandInvoker(
            TestCommandResolver testCommandResolver
    ) {
        this.testCommandResolver = testCommandResolver;
    }

    public String execute(TestType testType) {
        TestCommand command =
                testCommandResolver.resolve(testType);

        return command.execute();
    }
}