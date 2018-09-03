package pw.io.booker.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import pw.io.booker.exception.BookerServiceException;
import pw.io.booker.repo.AuthenticationRepository;

@Aspect
@Component
public class AuthenticationAspect {
	
	private AuthenticationRepository authenticationRepository;
	
	public AuthenticationAspect(AuthenticationRepository authenticationRepository) {
		super();
		this.authenticationRepository = authenticationRepository;
	}

	@Around("execution(* pw.io.booker.controller..*(..)) && args(token,..) && "
			+ "!execution(* pw.io.booker.controller.CustomerController.saveAll(..))")
	public Object aroundAuthenticationAspect(ProceedingJoinPoint pjp, String token) throws Throwable {
		if(token == null) {
			throw new BookerServiceException("Access denied!");
		}
		if(!authenticationRepository.findByToken(token).isPresent()) {
			throw new BookerServiceException("Access denied!");
		}
		return pjp.proceed();
	}
	
	@Before("execution(* pw.io.booker.controller..*(..)) && args(token,..)")
	public void beforeAuthenticationAspect(JoinPoint jp, String token) throws Throwable {
		Logger logger = Logger.getLogger(AuthenticationAspect.class);
		logger.info("Initializing the method: " + jp.getSignature().getName());
	}
	
	@After("execution(* pw.io.booker.controller..*(..)) && args(token,..)")
	public void afterAuthenticationAspect(JoinPoint jp, String token) throws Throwable {
		Logger logger = Logger.getLogger(AuthenticationAspect.class);
		logger.info("Successfully executed the method: " + jp.getSignature().getName());
	}
}
