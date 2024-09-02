package com.ccs.springaop.aop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@ConditionalOnProperty(value = "app.controllersheathmap.enabled", havingValue = "true")
public class ControllersHeatMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllersHeatMap.class);
    private final Map<String, Integer> healthMap = new ConcurrentHashMap<>();

    @Before("execution(* com.ccs.springaop.controller..*(..))")
    public void beforeController(JoinPoint joinPoint) {

        final String controllerName =
                joinPoint.getThis().getClass().getSimpleName()
                        .substring(0, joinPoint.getThis().getClass().getSimpleName().indexOf("$"))
                        .concat("#" + joinPoint.getSignature().getName());

        healthMap.put(controllerName, healthMap.getOrDefault(controllerName, 0) + 1);
        LOGGER.info("Controller {} called {} times", controllerName, healthMap.get(controllerName));
    }
}
