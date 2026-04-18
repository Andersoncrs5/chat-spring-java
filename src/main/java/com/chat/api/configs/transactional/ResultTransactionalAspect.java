package com.chat.api.configs.transactional;

import com.chat.api.utils.result.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
public class ResultTransactionalAspect {

    private final PlatformTransactionManager transactionManager;

    public ResultTransactionalAspect(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Around("@annotation(com.chat.api.utils.transactional.ResultTransactional)")
    public Object handleTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        TransactionStatus status =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Result<?> r) {
                if (r.isFailure()) {
                    transactionManager.rollback(status);
                    return result;
                }
            }

            if (!(result instanceof Result<?>)) {
                throw new IllegalStateException(
                        "@ResultTransactional só pode ser usado com retorno Result<T>"
                );
            }

            transactionManager.commit(status);
            return result;

        } catch (Throwable ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}