package at.ac.uibk.plant_health.models.annotations.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

import at.ac.uibk.plant_health.models.annotations.PrincipalRequired;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class PrincipalRequiredAspect {
	@Autowired
	private HttpServletRequest request;

	@Around("@annotation(at.ac.uibk.plant_health.models.annotations.PrincipalRequired)")
	public Object doSomething(ProceedingJoinPoint jp) throws Throwable {
		Class<?> requiredPrinciple = ((MethodSignature) jp.getSignature())
											 .getMethod()
											 .getAnnotation(PrincipalRequired.class)
											 .value();

		if (requiredPrinciple.isAssignableFrom(request.getUserPrincipal().getClass())) {
			return jp.proceed();
		}

		throw new AccessDeniedException(
				String.format("Required Principle %s", requiredPrinciple.getSimpleName())
		);
	}
}