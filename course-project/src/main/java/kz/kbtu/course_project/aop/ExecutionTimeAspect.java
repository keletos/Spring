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
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            logger.info("Method {} completed in {} ms",
                    method, (System.nanoTime() - startTime) / 1_000_000.0);
            return result;
        } catch (Throwable exception) {
            logger.error("Method {} failed after {} ms",
                    method, (System.nanoTime() - startTime) / 1_000_000.0, exception);
            throw exception;
        }
    }
}
