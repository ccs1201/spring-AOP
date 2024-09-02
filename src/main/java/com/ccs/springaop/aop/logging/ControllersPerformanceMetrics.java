package com.ccs.springaop.aop.logging;

import lombok.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@ConditionalOnProperty(value = "app.controllersperformance.enabled", havingValue = "true")
public class ControllersPerformance {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllersPerformance.class);

    private final Map<String, Telemetry> telemetryMap = new ConcurrentHashMap<>();

    @Around("execution(* com.ccs.springaop.controller..*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String controllerName = getControllerName(joinPoint);

        Object result = joinPoint.proceed();

        long totalTime = System.currentTimeMillis() - start;
        updateTelemetry(controllerName, totalTime);

        LOGGER.debug("Controller {} called {} times", controllerName, telemetryMap.get(controllerName).getExecutionCount());
        return result;
    }

    private String getControllerName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
    }

    private void updateTelemetry(String controllerName, long totalTime) {
        telemetryMap.compute(controllerName, (key, existingTelemetry) -> {
            if (existingTelemetry == null) {
                return new Telemetry(controllerName, totalTime, 1, totalTime, totalTime, totalTime, totalTime);
            }
            return existingTelemetry.update(totalTime);
        });
    }




    @Data
    @AllArgsConstructor
    private class Telemetry {

        private String controllerName;
        private long totalExecutionTime;
        private long executionCount;
        private long worst;
        private long best;
        private long last;
        private long average;

        public Telemetry update(long totalTime) {
            executionCount++;
            totalExecutionTime += totalTime;
            worst = Math.max(worst, totalTime);
            best = Math.min(best, totalTime);
            last = totalTime;
            average = totalExecutionTime / executionCount;
            return this;
        }
    }
}
