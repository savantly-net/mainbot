package net.savantly.mainbot.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggerAspect {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggerAspect.class);

	@Around("execution(* net.savantly.mainbot.dom..*(..))")
	public Object logAroundDomain(ProceedingJoinPoint joinPoint) throws Throwable{
		Object response = null;
		String method = joinPoint.getSignature().toShortString();
		try {
			log.debug("enter - " + method);
			response = joinPoint.proceed();
			log.debug("exit - " + method);
			return response;
		} catch (Exception e) {
			log.error("Exception while invoking method - " + method);
			throw e;
		}
	}

	@Around("execution(* net.savantly.mainbot.api..*(..))")
	public Object logAroundApi(ProceedingJoinPoint joinPoint) throws Throwable{
		Object response = null;
		String method = joinPoint.getSignature().toShortString();
		try {
			log.debug("enter - " + method);
			response = joinPoint.proceed();
			log.debug("exit - " + method);
			return response;
		} catch (Exception e) {
			log.error("Exception while invoking method - " + method);
			throw e;
		}
	}
}