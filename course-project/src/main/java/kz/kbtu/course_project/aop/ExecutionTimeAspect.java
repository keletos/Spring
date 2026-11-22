package kz.kbtu.course_project.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("execution(public * kz.kbtu.course_project.app.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint)
            throws Throwable {
                
        long startTime = System.nanoTime();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.nanoTime() - startTime;

            logger.info(
                    "Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime / 1_000_000.0
            );
        }
    }
}
