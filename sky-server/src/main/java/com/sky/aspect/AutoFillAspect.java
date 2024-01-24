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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){};

    @Before("pt()")
    public void before(JoinPoint joinPoint) throws Exception {
        log.info("开始进入AutoFill切面");

        // 获取注解及它的值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = annotation.value();

        // 获取参数,因为这里要的是具体的值，所以要用joinPoint.getArgs()
        Object[] args = joinPoint.getArgs();
        if (args==null || args.length==0) {
            return;
        }
        Object arg = args[0];

        // 调用参数的对应方法修改参数
        if (value==OperationType.INSERT){
            Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

            setCreateTime.invoke(arg, LocalDateTime.now());
            setCreateUser.invoke(arg, BaseContext.getCurrentId());
            setUpdateTime.invoke(arg, LocalDateTime.now());
            setUpdateUser.invoke(arg, BaseContext.getCurrentId());
        }
        else{
            Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
            setUpdateTime.invoke(arg, LocalDateTime.now());
            setUpdateUser.invoke(arg, BaseContext.getCurrentId());
        }
    }
}
