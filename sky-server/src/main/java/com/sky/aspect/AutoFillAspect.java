package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，用于在数据库操作执行前自动填充实体的公共字段。
 * 这包括创建时间、创建用户、更新时间和更新用户。
 */
@Aspect  // 定义这是一个切面类
@Component  // 将这个类定义为Spring组件，使其被Spring容器管理
@Slf4j  // Lombok注解，自动为类提供一个日志对象
public class AutoFillAspect {
    /**
     * 定义切入点，匹配所有在com.sky.mapper包下的类的方法，且这些方法被@AutoFill注解标记。
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 前置通知：在匹配的方法执行前执行这个方法。
     *
     * @param joinPoint 连接点，提供对当前方法执行点的访问。
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充处理...");

        // 使用AspectJ的API获取当前方法的签名，并转换为MethodSignature以获取访问方法级别元数据的能力。
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 通过反射获取当前方法上的@AutoFill注解，并进一步获取注解的value属性，即数据库操作类型。
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取当前方法的所有参数，假设第一个参数是需要自动填充字段的实体对象。
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        // 获取当前时间和用户ID，用于填充字段。
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据操作类型决定填充的字段。
        if (operationType == OperationType.INSERT) {
            try {
                // 反射获取并调用实体的setCreateTime和setCreateUser等方法。
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射设置字段的值。
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("自动填充插入字段时出错", e);
                throw new RuntimeException("Error during auto-fill for insert operation", e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                // 反射获取并调用实体的setUpdateTime和setUpdateUser方法。
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射设置字段的值。
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("自动填充更新字段时出错", e);
                throw new RuntimeException("Error during auto-fill for update operation", e);
            }
        }
    }
}
