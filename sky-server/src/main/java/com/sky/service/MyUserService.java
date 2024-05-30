package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.MyUserLoginDTO;
import com.sky.entity.Employee;
import com.sky.entity.MyUser;
import com.sky.result.PageResult;

public interface MyUserService {
    /**
     * 员工登录
     * @param myUserLoginDTO
     * @return
     */
    MyUser login(MyUserLoginDTO myUserLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
//    void save(EmployeeDTO employeeDTO);
}
