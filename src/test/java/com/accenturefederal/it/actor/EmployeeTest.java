package com.accenturefederal.it.actor;


import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class EmployeeTest {

    protected Employee employee;
    @Before
    public void setUp() {
        employee = new Employee(1234);
    }

    @Test
    public void testPlanRegularDay() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 8);
        Date startTime = new Date(cal.getTimeInMillis());
        cal.set(Calendar.HOUR,17);
        Date endTime = new Date(cal.getTimeInMillis());
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
        employee.planDay(startTime,endTime);
    }
}
