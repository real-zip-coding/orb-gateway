package com.orb.gateway.auth.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orb.gateway.auth.common.constraint.ErrorType;
import com.orb.gateway.auth.common.exception.Exceptions;
import com.orb.gateway.auth.common.exception.Exceptions.*;
import com.orb.gateway.auth.common.model.CommonResponse;
import com.orb.gateway.auth.config.db.ConfigConst;
import com.orb.gateway.auth.config.security.AuthenticationTokenProvider;
import com.orb.gateway.auth.entity.mysql.Member;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler extends CommonResponse {
    private final ApplicationContext context;
    private final AuthenticationTokenProvider authenticationTokenProvider;

    /**
     * 예외와 함께 스택 트레이스를 로깅
     *
     * @param message 로그에 출력할 커스텀 메시지
     * @param e 로깅할 예외
     */
    private void logException(String message, Exception e) {
        StringBuilder trace = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().contains(ConfigConst.EnvPath._basePackage_V1)) {
                trace.append("[")
                        .append(element.getFileName())
                        .append("-").append(element.getMethodName())
                        .append(":").append(element.getLineNumber())
                        .append("] ");
                break;
            }
        }
        trace.append(message).append(": ").append(e.getMessage());
        log.error(trace.toString());
    }

    /**
     * 예외를 처리하고 특정 HTTP 상태 코드로 응답 엔티티를 생성
     *
     * @param errorType 에러 객체
     * @param e 처리할 예외
     * @return 주어진 상태 코드와 예외 메시지를 포함하는 응답 엔티티
     */
    private ResponseEntity<?> handleException(ErrorType errorType, Exception e) {
        this.logException("Exception Occurred", e);
        return this.resFail(errorType);
    }

    /**
     * 일반적인 예외를 처리하고 활성화된 프로필에 따라 예외를 로깅
     *
     * @param e 처리할 일반 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleGeneralException(Exception e) {
        Environment environment = context.getEnvironment();
        if (ConfigConst.ApplicationConf.DEV_PROFILES.stream().anyMatch(environment::acceptsProfiles))
            log.error("env>dev | Exception TraceLog >>", e);
        else {
            // send webhook..?
        }
        return this.handleException(ErrorType.SERVER_ERROR, e);
    }

    /**
     * HTTP 메시지를 읽을 수 없는 경우의 예외를 처리
     *
     * @param e 처리할 HttpMessageNotReadableException
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return this.handleException(ErrorType.BAD_REQUEST, e);
    }

    /**
     * 접근이 거부된 경우의 예외를 처리하고 요청 URI와 이메일을 로깅
     *
     * @param request 요청에 포함된 HTTP 서블릿 요청
     * @param e 처리할 AccessDeniedException
     * @return HTTP 상태 401 Unauthorized를 포함하는 응답 엔티티
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDenied(HttpServletRequest request, AccessDeniedException e) {
        String email = getEmailFromRequest(request);
        String errorMessage = String.format("UnauthorizedException uri: %s, email: %s", request.getRequestURI(), email);
        this.logException(errorMessage, e);
        return this.handleException(ErrorType.ACCESS_DENIED, e);
    }

    /**
     * 요청의 인증 헤더에서 이메일을 추출
     *
     * @param request HTTP 서블릿 요청
     * @return 토큰에서 추출한 이메일, 또는 토큰이 없으면 null
     */
    private String getEmailFromRequest(HttpServletRequest request) {
        if (!ObjectUtils.isEmpty(request.getHeader("Authorization"))) {
            String tokenStr = authenticationTokenProvider.parseTokenString(request);
            Claims claims = authenticationTokenProvider.getClaim(tokenStr);
            return authenticationTokenProvider.getAuthentication(claims).getName();
        }
        return null;
    }

    /**
     * 유효성 검사 예외를 처리하고 필드 오류를 반환
     *
     * @param e 처리할 MethodArgumentNotValidException
     * @return 유효성 검사 오류 상세를 포함한 HTTP 상태 400 Bad Request 응답 엔티티
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        }
        return this.resFail(ErrorType.ARGUMENT_NOT_VALID, errors);
    }

    /**
     * 유효성 검사 예외를 처리하고 필드 오류를 반환
     *
     * @param e 처리할 ConstraintViolationException
     * @return 유효성 검사 오류 상세를 포함한 HTTP 상태 400 Bad Request 응답 엔티티
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(ConstraintViolationException e) {
        Map<String, Object> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return this.resFail(ErrorType.ARGUMENT_NOT_VALID, errors);
    }


    /**
     * 필드 유형 오류를 반환
     *
     * @param e 처리할 MethodArgumentTypeMismatchException
     * @return 유효성 검사 오류 상세를 포함한 HTTP 상태 400 Bad Request 응답 엔티티
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        return this.resFail(ErrorType.ARGUMENT_NOT_VALID);
    }

    /**
     * 유효성 검사 예외를 처리
     * @param e ValidationException 예외
     * @return 유효성 검사 오류 상세를 포함한 HTTP 상태 400 Bad Request 응답 엔티티
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        return this.resFail(ErrorType.ARGUMENT_NOT_VALID);
    }

    /**
     * parameter 의 유효성 검사 이후 발생한 예외처리
     *
     * @param e 처리할 MissingServletRequestParameterException
     * @return 유효성 검사 오류 상세를 포함한 HTTP 상태 400 Bad Request 응답 엔티티
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MissingServletRequestParameterException e) {
        return this.handleException(ErrorType.MISSING_PARAMETER, e);
    }

    /**
     * 인증 관련 예외를 처리
     *
     * @param e 인증 관련 예외
     * @return HTTP 상태 401 Unauthorized를 포함하는 응답 엔티티
     */
    @ExceptionHandler({
            NotFoundAuth.class,
            BadCredentialsException.class
    })
    protected ResponseEntity<?> handleNotFoundAuth(Exception e) {
        return this.handleException(ErrorType.ACCESS_DENIED, e);
    }

    /**
     * SSN중복 예외를 처리
     *
     * @param e SSN 검증 예외처리
     * @return HTTP 상태 401 Unauthorized를 포함하는 응답 엔티티
     */
    @ExceptionHandler({DuplicateSsnException.class})
    protected ResponseEntity<?> DuplicateSsnException(DuplicateSsnException e) {
        return this.handleException(ErrorType.SSN_IS_ALREADY_EXISTS, e);
    }

    /**
     * 요청한 데이터를 찾을 수 없는 경우의 예외를 처리
     *
     * @param e NotFoundData 예외
     * @return HTTP 상태 404 Not found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(NotFoundData.class)
    protected ResponseEntity<?> handleNotFoundData(NotFoundData e) {
        return this.handleException(ErrorType.NOT_FOUND, e);
    }

    /**
     * URI 구문 예외를 처리
     *
     * @param e URISyntaxException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(URISyntaxException.class)
    protected ResponseEntity<?> handleURISyntax(URISyntaxException e) {
        return this.handleException(ErrorType.BAD_REQUEST, e);
    }

    /**
     * HTTP 클라이언트 오류 예외를 처리
     *
     * @param e HttpClientErrorException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<?> handleHttpClientError(HttpClientErrorException e) {
        return this.handleException(ErrorType.BAD_REQUEST, e);
    }

    /**
     * API 응답 상태가 유효하지 않을 때의 예외를 처리
     *
     * @param e APIResponseStatusNotValid 예외
     * @return HTTP 상태 204 No Content를 포함하는 응답 엔티티
     */
    @ExceptionHandler(APIResponseStatusNotValid.class)
    protected ResponseEntity<?> handleAPIResponseStatusNotValid(APIResponseStatusNotValid e) {
        this.logException("APIResponseStatusNotValid", e);
        return this.resSuccessNoContents();
    }

    /**
     * 입출력 예외를 처리
     *
     * @param e IOException 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<?> handleIOException(IOException e) {
        return this.handleException(ErrorType.SERVER_ERROR, e);
    }

    /**
     * 예기치 않은 타입 예외를 처리
     *
     * @param e UnexpectedTypeException 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    protected ResponseEntity<?> handleUnexpectedType(UnexpectedTypeException e) {
        return this.handleException(ErrorType.SERVER_ERROR, e);
    }

    /**
     * 런타임 예외를 처리
     *
     * @param e 처리할 런타임 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler({
            NoSuchElementException.class,
            Exceptions.IllegalArgumentException.class,
            IllegalStateException.class
    })
    protected ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return this.handleException(ErrorType.BAD_REQUEST, e);
    }

    /**
     * JWT 토큰 예외를 처리
     *
     * @param e JwtTokenException 예외
     * @return HTTP 상태 401 Unauthorized를 포함하는 응답 엔티티
     */
    @ExceptionHandler(JwtTokenException.class)
    protected ResponseEntity<?> handleJwtTokenException(JwtTokenException e) {
        return this.handleException(ErrorType.ACCESS_DENIED, e);
    }

    /**
     * Redis 예외를 처리
     *
     * @param e RedisOperationException 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(RedisOperationException.class)
    protected ResponseEntity<?> handleRedisOperationException(RedisOperationException e) {
        return this.handleException(ErrorType.SERVER_ERROR, e);
    }

    /**
     * JSON 처리 예외를 처리
     *
     * @param e JsonProcessingException 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<?> handleJsonProcessingException(JsonProcessingException e) {
        return this.handleException(ErrorType.SERVER_ERROR, e);
    }

    /**
     * 소셜로그인시 중복 사용자 예외를 처리 (email 중복체크)
     *
     * @param e DuplicateUserException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(DuplicateUserAuthException.class)
    protected ResponseEntity<?> handleUserAlreadyExistsException(DuplicateUserAuthException e) {
        Member.AuthType authType = e.getAuthType();

        if (authType.equals(Member.AuthType.EMAIL)) {
            return this.handleException(ErrorType.MEMBER_IS_ALREADY_EXISTS, e);
        } else {
            return this.handleException(ErrorType.MEMBER_IS_ALREADY_ANOTHER_SOCIAL, e);
        }
    }

    /**
     * 사용자 ROLE 확인 실패 예외를 처리
     *
     * @param e MemberRoleNotDeterminedException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(MemberRoleNotDeterminedException.class)
    protected ResponseEntity<?> memberRoleNotDeterminedException(MemberRoleNotDeterminedException e) {
        return this.handleException(ErrorType.MEMBER_ROLE_IS_NOT_DETERMINED, e);
    }

    /**
     * 사용자 이름을 찾을 수 없는 예외를 처리
     *
     * @param e UsernameNotFoundException 예외
     * @return HTTP 상태 404 Not Found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return this.handleException(ErrorType.NOT_FOUND, e);
    }

    /**
     *
     * @param e UsernameNotFoundException 예외
     * @return HTTP 상태 404 Not Found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(LockedCertificationException.class)
    protected ResponseEntity<?> handleAllowedLimitException(LockedCertificationException e) {
        return this.resFailSendMessage(ErrorType.LOCKED_VERIFICATION);
    }

    /**
     *
     * @param e UsernameNotFoundException 예외
     * @return HTTP 상태 404 Not Found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return this.resFail(ErrorType.METHOD_NOT_ALLOWED);
    }

    /**
     * @param e VerificationCodeExpiredException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(VerificationCodeExpiredException.class)
    protected ResponseEntity<?> handleVerificationCodeExpiredException(VerificationCodeExpiredException e) {
        return this.resFailSendMessage(ErrorType.VERIFICATION_CODE_EXPIRED);
    }

    /**
     *
     * @param e PasswordResetTimeoutException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(PasswordResetTimeoutException.class)
    protected ResponseEntity<?> handlePasswordResetTimeoutException(PasswordResetTimeoutException e) {
        return this.resFailSendMessage(ErrorType.PASSWORD_RESET_TIMEOUT);
    }

    /**
     *
     * @param e InvalidPasswordException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<?> handlePasswordResetInvalidException(InvalidPasswordException e) {
        return this.resFailSendMessage(ErrorType.NOT_VALID_PASSWORD);
    }

    /**
     *
     * @param e NotValidCodeException 예외
     * @return HTTP 상태 404 Not Found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(NotValidCodeException.class)
    protected ResponseEntity<?> notValidCodeException(NotValidCodeException e) {
        return this.resFail(ErrorType.NOT_VALID_CODE);
    }

    /**
     *
     * @param e UsernameNotFoundException 예외
     * @return HTTP 상태 404 Not Found를 포함하는 응답 엔티티
     */
    @ExceptionHandler(NotValidNumberException.class)
    protected ResponseEntity<?> notValidNumberException(NotValidNumberException e) {
        return this.resFail(ErrorType.NOT_VALID_NUMBER);
    }

    /**
     * 회원 계정탈퇴 실패 예외를 처리
     * @param e MemberAccountDeleteException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(MemberAccountDeleteException.class)
    protected ResponseEntity<?> memberAccountDeleteException(MemberAccountDeleteException e) {
        return this.resFailSendMessage(ErrorType.MEMBER_ACCOUNT_DELETE_FAIL);
    }

    /**
     * 탈퇴 회원 예외를 처리
     * @param e MemberAlreadyDeletedException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(MemberAlreadyDeletedException.class)
    protected ResponseEntity<?> memberAlreadyDeletedException(MemberAlreadyDeletedException e) {
        return this.resFailSendMessage(ErrorType.MEMBER_ALREADY_DELETED);
    }

    /**
     * Redis TTL 예외를 처리
     *
     * @param e RedisTTLException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(RedisTTLException.class)
    protected ResponseEntity<?> redisTTLException(RedisTTLException e) {
        return this.resFailSendMessage(ErrorType.EXIST_VERIFICATION_CODE);
    }

    /**
     * 애플 OAuth 예외를 처리
     * @param e AppleOauth2Exception 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(AppleOauth2Exception.class)
    protected ResponseEntity<?> appleOAuthException(AppleOauth2Exception e) {
        return this.resFailSendMessage(ErrorType.BAD_REQUEST, e.getMessage());
    }

    /**
     * 사용자가 요청한 데이터 검증에 대한 예외 처리
     * @param e InvalidDataException 예외
     * @return HTTP 상태 400 Bad Request를 포함하는 응답 엔티티
     */
    @ExceptionHandler(InvalidDataException.class)
    protected ResponseEntity<?> invalidDataException(InvalidDataException e) {
        return this.handleException(ErrorType.BAD_REQUEST, e);
    }

    /**
     * Firebase SDK 초기화 실패 예외를 처리
     *
     * @param e FirebaseSDKException 예외
     * @return HTTP 상태 500 Internal Server Error를 포함하는 응답 엔티티
     */
    @ExceptionHandler(FirebaseSDKException.class)
    protected ResponseEntity<?> firebaseSDKException(FirebaseSDKException e) {
        return this.resFailSendMessage(ErrorType.FIREBASE_SDK_INIT_FAIL);
    }
}
