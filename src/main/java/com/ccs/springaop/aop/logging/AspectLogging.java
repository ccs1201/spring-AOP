package com.ccs.springaop.aop.logging;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@ConditionalOnProperty(value = "app.aspectlogging.enabled", havingValue = "true")
@Slf4j
public class AspectLogging {

    public AspectLogging() {
        log.info("AspectLogging - initialized");
    }

    /**
     * O primeiro argumento dentro de execution() é o tipo de retorno do método
     * O segundo é o nome do pacote onde a classe alvo está (com.ccs.springaop..)
     * O terceiro é o nome do método (* após o com.ccs.springaop..)
     * O quarto é o tipo de parâmetros (argumentos) do método (..*(..))
     * <p>
     * Então temos execution(* com.ccs.springaop..*(..) ) que significa:
     * Qualquer retorno (*), em qualquer classe do pacote com.ccs.springaop (..)
     * e qualquer método (*) com qualquer parâmetro (..)
     */
    @Before("execution(* com.ccs.springaop..*(..))")
    public void logBeforeCall(JoinPoint joinPoint) {
        log.info("@Before -> Iniciando execução do método -> {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());
    }

    @Around("execution(* com.ccs.springaop..*(..))")
    public Object logAfterCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object returnValue = proceedingJoinPoint.proceed();
        log.info("@Around -> Finalizando execução do método -> {}.{} Tipo do retorno -> {}",
                proceedingJoinPoint.getTarget().getClass().getSimpleName(),
                proceedingJoinPoint.getSignature().getName(),
                Objects.isNull(returnValue) ? null : returnValue.getClass().getSimpleName());
        return returnValue;
    }
}
