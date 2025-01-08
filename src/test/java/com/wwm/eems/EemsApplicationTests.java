package com.wwm.eems;

import com.wwm.eems.mapper.*;
import com.wwm.eems.model.request.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

@SpringBootTest
class EemsApplicationTests {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;


    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);

    @Test
    void test() {
        LocalTime checkInTime = LocalTime.now();

        System.out.println("=============================");
        System.out.println(checkInTime);
        System.out.println(WORK_START_TIME);
        System.out.println(checkInTime.isAfter(WORK_START_TIME));
//        attendance.setStatus(checkInTime.isAfter(WORK_START_TIME) ? 1 : 2);
    }

}
