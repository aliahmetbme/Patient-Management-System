package com.aliahmet.pms.testorder.command;

import com.aliahmet.pms.testorder.TestCommandInvoker;
import com.aliahmet.pms.testorder.TestType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TestCommandInvokerTest {

    @Autowired
    private TestCommandInvoker testCommandInvoker;

    @Test
    void xRayCommandShouldBeExecuted() {

        String result =
                testCommandInvoker.execute(TestType.X_RAY);

        assertEquals(
                "X-RAY was performed by Radiology Department",
                result
        );
    }
}