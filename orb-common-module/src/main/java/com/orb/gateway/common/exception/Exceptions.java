package com.orb.gateway.common.exception;

import lombok.Getter;
import com.orb.gateway.common.entity.mysql.Member;

/**
 * exception 정의 class
 */
public class Exceptions {

    /**
     * GlobalExceptionHandler 에서 exception trace 를 로깅 하고 싶지 않을 때 상속 하여 사용
     */
    public static class NoTrace extends RuntimeException {
        public NoTrace(String msg) {
            super(msg);
        }

        public NoTrace(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * 데이터정보없음
     */
    public static class NotFoundData extends NoTrace {
        public NotFoundData() {
            super(NotFoundData.class.getSimpleName());
        }

        public NotFoundData(String msg) {
            super(msg);
        }
    }

    /**
     * 권한정보없음
     */
    public static class NotFoundAuth extends NoTrace {
        public NotFoundAuth() {
            super(NotFoundAuth.class.getSimpleName());
        }
    }

    /**
     * 응답 status 코드확인 실패
     */
    public static class APIResponseStatusNotValid extends NoTrace {
        public APIResponseStatusNotValid(String msg) {
            super(msg);
        }
    }

    /**
     * Google 인증 실패 및 사용자정보획득 실패
     */
    public static class GoogleOauth2Exception extends RuntimeException {
        public GoogleOauth2Exception(String msg) {
            super(msg);
        }
    }

    /**
     * Apple 인증 실패 및 사용자정보획득 실패
     */
    public static class AppleOauth2Exception extends RuntimeException {
        public AppleOauth2Exception(String msg) {
            super(msg);
        }
    }

    /**
     * FirebaseSDK 연동 에러
     */
    public static class FirebaseSDKException extends NoTrace {
        public FirebaseSDKException(String msg) {
            super(msg);
        }
    }

    /**
     * 요청 정보 검증 실패
     */
    public static class NoSuchElementException extends NoTrace {
        public NoSuchElementException(String msg) {
            super(msg);
        }
    }

    /**
     * 요청 정보 검증 실패
     */
    public static class IllegalArgumentException extends NoTrace {
        public IllegalArgumentException(String msg) {
            super(msg);
        }
    }

    /**
     * social-login 실패
     */
    @Getter
    public static class DuplicateUserAuthException extends NoTrace {
        private final Member.AuthType authType;

        public DuplicateUserAuthException(String msg, Member.AuthType authType) {
            super(msg);
            this.authType = authType;
        }

    }

    /**
     * social-login 실패
     */
    @Getter
    public static class DuplicateSsnException extends NoTrace {
        public DuplicateSsnException(String msg) {super(msg);}
    }

    /**
     * 사용자 권한 정보 확인 실패
     */
    public static class MemberRoleNotDeterminedException extends NoTrace {
        public MemberRoleNotDeterminedException(String msg) {
            super(msg);
        }
    }

    /**
     * 인증 실패
     */
    public static class BadCredentialsException extends NoTrace {
        public BadCredentialsException(String msg) {
            super(msg);
        }
    }

    /**
     * redis 저장 실패
     */
    public static class RedisOperationException extends NoTrace {
        public RedisOperationException(String msg) {
            super(msg);
        }
    }

    /**
     * 토큰 검증 실패
     */
    public static class JwtTokenException extends NoTrace {
        public JwtTokenException(String msg) {
            super(msg);
        }
    }

    /**
     * 인증 한도 초과
     */
    public static class LockedCertificationException extends NoTrace {
        public LockedCertificationException(String msg) {
            super(msg);
        }
    }

    /**
     * 인증코드 검증 실패
     */
    public static class NotValidCodeException extends NoTrace {
        public NotValidCodeException(String msg) {
            super(msg);
        }
    }

    /**
     * 인증코드 만료
     */
    public static class VerificationCodeExpiredException extends NoTrace {
        public VerificationCodeExpiredException(String msg) {
            super(msg);
        }
    }

    /**
     * 패스워드 재설정 요청 만료
     */
    public static class PasswordResetTimeoutException extends NoTrace {
        public PasswordResetTimeoutException(String msg) {
            super(msg);
        }
    }

    /**
     * 패스워드 재설정 유효성 검증 실패
     */
    public static class InvalidPasswordException extends NoTrace {
        public InvalidPasswordException(String msg) {
            super(msg);
        }
    }

    /**
     * 전화번호 검증 실패
     */
    public static class NotValidNumberException extends NoTrace {
        public NotValidNumberException(String msg) {
            super(msg);
        }
    }

    /**
     * 회원 계정탈퇴 실패
     */
    public static class MemberAccountDeleteException extends NoTrace {
        public MemberAccountDeleteException(String msg) {
            super(msg);
        }
    }

    /**
     * 회원 계정탈퇴 후 이미 삭제된 회원 정보 접근 시도
     */
    public static class MemberAlreadyDeletedException extends NoTrace {
        public MemberAlreadyDeletedException(String msg) {
            super(msg);
        }
    }

    /**
     * Redis 타임아웃
     */
    public static class RedisTTLException extends NoTrace {
        public RedisTTLException(String msg) {
            super(msg);
        }
    }

    /**
     * Type 검증 실패
     */
    public static class InvalidDataException extends NoTrace {
        public InvalidDataException(String msg) {
            super(msg);
        }
    }
}