package com.sunner.structure.aop;

import com.sunner.structure.common.ResponseEnum;
import com.sunner.structure.common.Result;
import com.sunner.structure.exception.SunnerRuntimeException;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SudokuAdvice {
    private static final Logger log = Logger.getLogger(SudokuAdvice.class);

    @Pointcut("execution(* *..*Controller.*(..))")
    public void pt1() {
    }

    @Pointcut("execution(* *..impl.*.*(..))")
    public void pt2() {
    }

    @Pointcut("pt1() || pt2()")  //多个切入点表达式可以用 &&  ||  ! 连接
    public void pt3() {
    }

    /**
     * 环绕通知(异常捕获,将异常信息封装到Result中)
     */
    @Around("pt3()")
    public Result around4Error(ProceedingJoinPoint pjp) {
        try {
            return (Result) pjp.proceed();
        } catch (Throwable t) {
            SunnerRuntimeException sunnerException = null;
            if (t instanceof SunnerRuntimeException) {
                sunnerException = (SunnerRuntimeException) t;
                log.error("errorCode:" + sunnerException.getErrCode() + " errorMessage:" + sunnerException.getMessage(), sunnerException);
            } else {
                sunnerException = new SunnerRuntimeException(ResponseEnum.EXCEPTION);
                log.error("errorCode:" + sunnerException.getErrCode() + " errorMessage:" + sunnerException.getMessage(), t);
            }
            return Result.fail(sunnerException.getErrCode(), sunnerException.getMessage());
        }
    }

    /**
     * 环绕通知(异常捕获,将异常信息封装到Result中)
     */
    @Around("pt1()")
    public Result around4ControllerReturn(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        Result result = (Result) pjp.proceed();
        log.info(className+"."+methodName+"  returnCode:" + result.getCode() + "  returnMsg:" + result.getMsg());
        return result;
    }
}
