package com.orb.gateway.auth.common.annotation;

import com.orb.gateway.auth.common.constraint.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Operation
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
})

@ApiErrorCodeExample({})
public @interface SwaggerDefaultInfoAnnotation {
	@AliasFor(annotation = Operation.class, attribute = "description")
	String title() default "";
	String summary() default "";

	@AliasFor(annotation = ApiErrorCodeExample.class, attribute = "value")
	ErrorType[] errorCodes() default {};
}