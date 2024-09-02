package com.ccs.springaop.aop.metrics;

import jakarta.annotation.PreDestroy;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@ConditionalOnProperty(value = "app.controllersperformancemetrics.enabled", havingValue = "true")
@Slf4j
public class ControllersPerformanceMetrics {

    public ControllersPerformanceMetrics() {
        log.info("###-> ControllersPerformanceMetrics - initialized");
    }

    private final Map<String, Telemetry> telemetryMap = new ConcurrentHashMap<>();

    @PreDestroy
    public void destroy() {
        telemetryMap.forEach((s, telemetry) -> log.info("###-> ControllersPerformanceMetrics - {}", telemetry));
    }

    @Around("execution(* com.ccs.springaop.controller..*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        String endpointMethodName = getendpointMethodName(joinPoint);

        Object result = joinPoint.proceed();

        long totalTime = System.nanoTime() - start;
        updateTelemetry(endpointMethodName, totalTime);

        log.info("Controller {} executado {} vezes Retornando: {}",
                endpointMethodName, telemetryMap.get(endpointMethodName).getExecutionCount(), result);
        return result;
    }

    private String getendpointMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
    }

    private void updateTelemetry(String endpointMethodName, long totalTime) {
        telemetryMap.compute(endpointMethodName, (key, existingTelemetry) -> {
            if (Objects.isNull(existingTelemetry)) {
                return Telemetry.builder().
                        endpointMethodName(endpointMethodName)
                        .totalExecutionTime(totalTime)
                        .executionCount(1L)
                        .worst(totalTime)
                        .best(totalTime)
                        .last(totalTime)
                        .average((double) totalTime)
                        .build();
            }
            return existingTelemetry.update(totalTime);
        });
    }

    @Builder
    private static class Telemetry {

        private String endpointMethodName;
        private long totalExecutionTime;
        @Getter
        private long executionCount;
        private long worst;
        private long best;
        private long last;
        private double average;

        public Telemetry update(long totalTime) {
            synchronized (this) {
                executionCount++;
                totalExecutionTime += totalTime;
                worst = Math.max(worst, totalTime);
                best = Math.min(best, totalTime);
                last = totalTime;
                average = totalExecutionTime / (double) executionCount;
                return this;
            }
        }

        @Override
        public String toString() {
            return "Telemetry{"
                    .concat("controllerName=" + endpointMethodName + '\'')
                    .concat(", totalExecutionTime=" + (totalExecutionTime / 1_000_000)).concat("ms")
                    .concat(", executionCount=" + executionCount)
                    .concat(", worst=" + worst).concat("ns")
                    .concat(", best=" + best).concat("ns")
                    .concat(", last=" + last).concat("ns")
                    .concat(", average=" + average).concat("ns")
                    .concat("'}'");
        }
    }
}
