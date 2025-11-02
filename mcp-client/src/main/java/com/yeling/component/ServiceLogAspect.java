package com.yeling.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author 夜凌
 * @ClassName ServiceLogAspect
 * @Date 2025/9/30 18:50
 * @Version 1.0
 */
@Component
@Slf4j
@Aspect
class ServiceLogAspect {

    /**
     * @description: 记录方法执行时间
     * @author: 夜凌
     * @date: 2025/9/30 19:04
     * @param: [joinPoint]
     * @return: Object
     **/
    @Around("execution(* com.yeling.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        String point = joinPoint.getSignature().getName()
                + "."
                + joinPoint.getSignature().getName();


        stopWatch.stop();

        long elapsedTime = stopWatch.getTotalTimeMillis();


        if (elapsedTime > 3000) {
            log.error("{} 耗时 {} 毫秒", point, elapsedTime);
        } else if (elapsedTime > 2000) {
            log.warn("{} 耗时 {} 毫秒", point, elapsedTime);
        } else {
            log.info("{} 耗时 {} 毫秒", point, elapsedTime);
        }
        return result;
    }
}
