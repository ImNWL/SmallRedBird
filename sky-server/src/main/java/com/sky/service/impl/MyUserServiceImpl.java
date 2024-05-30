package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.MyUserLoginDTO;
import com.sky.entity.Employee;
import com.sky.entity.MyUser;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.mapper.MyUserMapper;
import com.sky.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyUserServiceImpl implements MyUserService {

    @Autowired
    private MyUserMapper myUserMapper;


    /**
     * 员工登录
     *
     * @param myUserLoginDTO
     * @return
     */
    public MyUser login(MyUserLoginDTO myUserLoginDTO) {
        String name = myUserLoginDTO.getName();
        String password = myUserLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        MyUser user = myUserMapper.getByName(name);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return user;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
//    public void save(EmployeeDTO employeeDTO) {
//        Employee employee = new Employee();
//
//        // 对象属性拷贝
//        BeanUtils.copyProperties(employeeDTO, employee);
//
//        employee.setStatus(StatusConstant.ENABLE);
//        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
//
//        employeeMapper.insert(employee);
//    }
}
