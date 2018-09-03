package pw.io.booker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
			throw new RuntimeException("Access denied!");
		}
		if(!authenticationRepository.findByToken(token).isPresent()) {
			throw new RuntimeException("Access denied!");
		}
		return pjp.proceed();
	}
}
