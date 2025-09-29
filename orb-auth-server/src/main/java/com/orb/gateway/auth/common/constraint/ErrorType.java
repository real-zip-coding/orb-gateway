package com.orb.gateway.auth.common.constraint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    //Common > 5xx
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "", "Server error"),
    //Common > 4xx
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 1001, "", "bad request"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 1001, "", "bad request"),
    ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, 1002, "", "argument not valid"),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, 1003, "", "missing parameter"),
    NO_SEARCH_DATA(HttpStatus.BAD_REQUEST, 1004, "", "no search data"),
    NOT_FOUND(HttpStatus.NOT_FOUND, 1005, "", "not found"),
    TOO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, 1006, "", "too many request"),
    CONFLICT(HttpStatus.CONFLICT, 1007, "", "conflict"),

    //Auth
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, 2001, "", "Unauthorized"),
    SEND_EMAIL_FAIL(HttpStatus.BAD_REQUEST, 2002, "", "Failed to send email"),
    EXIST_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, 2003, "", "You can request a new verification code in 5 minutes. Please wait before trying again."),
    LOCKED_VERIFICATION(HttpStatus.BAD_REQUEST, 2004, ErrorTitleConst.TOO_MANY_ATTEMPTS, "Try again in 5 minutes. Ensure your phone number is correct or get help."),
    NOT_VALID_NUMBER(HttpStatus.BAD_REQUEST, 2005, "", "Phone number is not a valid"),
    NOT_VALID_CODE(HttpStatus.BAD_REQUEST, 2006, "", "Verification code is not valid"),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, 2007, ErrorTitleConst.VERIFICATION_CODE_EXPIRED, "Verification code is expired"),
    PASSWORD_RESET_TIMEOUT(HttpStatus.BAD_REQUEST, 2008, ErrorTitleConst.PASSWORD_RESET_TIMEOUT, "Reset password timeout"),
    NOT_VALID_PASSWORD(HttpStatus.BAD_REQUEST, 2009, "", "Password is not valid"),
    SSN_IS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 2010, "", "SSN already exists"),

    //Member
    MEMBER_IS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 3001, "", "Member already exists by email"),
    MEMBER_IS_ALREADY_ANOTHER_SOCIAL(HttpStatus.BAD_REQUEST, 3002, "", "Signed up for another Social type"),
    MEMBER_ROLE_IS_NOT_DETERMINED(HttpStatus.BAD_REQUEST, 3003, "", " Member's role has not been determined."),
    MEMBER_ACCOUNT_DELETE_FAIL(HttpStatus.BAD_REQUEST, 3004, "", "Member account delete failed"),
    MEMBER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, 3005 , "" , "Member already deleted" ),

    //NorthCapital > CreateTraderProfile
    NORTH_CAPITAL_PROC_NOT_READY(HttpStatus.BAD_REQUEST, 4000, "", "NorthCapital > process not ready"),
    NORTH_CAPITAL_ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST, 4001, "", "NorthCapital > Account not found(Ready)"),
    NORTH_CAPITAL_REGISTERED_SSN_NUMBER(HttpStatus.BAD_REQUEST, 4002, "", "NorthCapital > SSN number already registered"),
    NORTH_CAPITAL_REQUEST_FAIL(HttpStatus.BAD_REQUEST, 4003, "", "NorthCapital > Request/Response fail"),
    NORTH_CAPITAL_INTERNAL_PROC_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 4004, "", "NorthCapital > internal process fail"),

    //NorthCapital > Purchase
    NORTH_CAPITAL_NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, 6010, "", "Not enough balance"),
    NORTH_CAPITAL_OVER_LIMIT_PURCHASE(HttpStatus.BAD_REQUEST, 6011, "", "over limit purchase security count"),
    NORTH_CAPITAL_NOT_ENOUGH_DROP(HttpStatus.BAD_REQUEST, 6012, "", "Not enough security drop count"),

    //KoreConX
    KORECONX_REQUEST_FAIL(HttpStatus.BAD_REQUEST, 5001, "", "KoreConX > Request fail"),
    KORECONX_INTERNAL_PROC_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "", "KoreConX > internal process fail"),

    //FirebaseSDK
    FIREBASE_SDK_INIT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 7001, "", "Firebase SDK init fail"),

    //twilio
    TWILIO_REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 8001, "", "Twilio SMS send message fail"),
    ;

    private final HttpStatus status;
    private final int code;
    private final String title;
    private final String message;
}