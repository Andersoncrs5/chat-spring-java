package com.chat.api.configs.api.idempotent;

import com.chat.api.configs.security.UserPrincipal;
import com.chat.api.utils.exceptions.BusinessRuleException;
import com.chat.api.utils.exceptions.ConflictRuleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    @Around("@annotation(idempotent)")
    public Object execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String headerKey = request.getHeader("X-Idempotency-Key");
        if (headerKey == null || headerKey.isBlank()) {
            throw new BusinessRuleException("Header X-Idempotency-Key is required.", HttpStatus.FORBIDDEN);
        }

        UUID userId = extractUserId(joinPoint.getArgs());

        String fullKey = "idempotency:"+ userId +":" + headerKey;

        log.info("Trying to get lock to the key: {}", fullKey);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
                fullKey,
                "PROCESSING",
                Duration.ofSeconds(idempotent.expire())
        );
        log.info("Lock got? {}", acquired);

        if (Boolean.FALSE.equals(acquired)) {
            String status = redisTemplate.opsForValue().get(fullKey);
            if ("PROCESSING".equals(status)) {
                throw new ConflictRuleException("Request is already being processed.");
            }
            throw new ConflictRuleException("Duplicate request detected for this user.");
        }

        try {
            Object result = joinPoint.proceed();

            redisTemplate.opsForValue().set(fullKey, "COMPLETED", Duration.ofSeconds(idempotent.expire()));

            return result;
        } catch (Throwable e) {
            if (idempotent.deleteKeyOnException()) {
                redisTemplate.delete(fullKey);
            }
            throw e;
        }
    }

    private UUID extractUserId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UserPrincipal principal) {
                return principal.getId();
            }
        }
        throw new BusinessRuleException("Idempotency failed: UserPrincipal not found in method arguments.");
    }
}