package com.aliahmet.pms;

import com.aliahmet.pms.department.RadiologyDepartment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class RadiologyDepartmentSingletonTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void radiologyDepartmentShouldBeSingleton() {

        RadiologyDepartment first =
                applicationContext.getBean(RadiologyDepartment.class);

        RadiologyDepartment second =
                applicationContext.getBean(RadiologyDepartment.class);

        assertSame(first, second);
    }
}