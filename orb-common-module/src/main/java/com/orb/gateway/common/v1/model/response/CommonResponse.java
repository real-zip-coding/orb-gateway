package com.orb.gateway.common.v1.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.orb.gateway.common.constraint.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
public class CommonResponse {
    public ResponseEntity<String> resSuccess(String contents) {
        return ResponseEntity.ok().body(contents);
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<ResSuccessPattern<T>> resSuccess(T contents) {
        return ResponseEntity.ok(
                (ResSuccessPattern<T>) ResSuccessPattern.builder()
                        .data(contents)
                        .build());
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<ResSuccessPattern<T>> resAdminSuccess(T contents, int count) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", String.valueOf(count));
        return ResponseEntity.ok()
                .headers(headers)
                .body(
                        (ResSuccessPattern<T>) ResSuccessPattern.builder()
                                .data(contents)
                                .build()
                );
    }

    /** REST API(GET) http status 200 code return */
    public ResponseEntity<Void> resSuccessNoContents() {
        return ResponseEntity.ok().body(null);
    }

    /** REST API(DELETE) http status 204 code return */
    public ResponseEntity<Void> resDeleteSuccessNoContents() {
        return ResponseEntity.noContent().build();
    }

    /** REST API http status 302 code return **/
    public ResponseEntity<Void> resFound(String redirectUrl) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    public ResponseEntity<ResFailPattern> resFail(ErrorType errorType) {
        return ResponseEntity
                .status(errorType.getStatus())
                .body(
                        ResFailPattern.builder()
                                .code(this.generateCode(errorType.getStatus().value(), errorType.getCode()))
                                .title(errorType.getTitle())
                                .message(errorType.getMessage())
                                .build());
    }

    public ResponseEntity<ResFailPattern> resFail(ErrorType errorType, Map<String, Object> errors) {
        return ResponseEntity
                .status(errorType.getStatus())
                .body(
                        ResFailPattern.builder()
                                .code(this.generateCode(errorType.getStatus().value(), errorType.getCode()))
                                .title(errorType.getTitle())
                                .message(errorType.getMessage())
                                .errors(errors)
                                .build());
    }

    public ResponseEntity<ResFailPattern> resFailSendMessage(ErrorType errorType) {
        return ResponseEntity
                .status(errorType.getStatus())
                .body(
                        ResFailPattern.builder()
                                .code(this.generateCode(errorType.getStatus().value(), errorType.getCode()))
                                .title(errorType.getTitle())
                                .message(errorType.getMessage())
                                .build());
    }

    public ResponseEntity<ResFailPattern> resFailSendMessage(ErrorType errorType, String errMsg) {
        return ResponseEntity
                .status(errorType.getStatus())
                .body(
                        ResFailPattern.builder()
                                .code(this.generateCode(errorType.getStatus().value(), errorType.getCode()))
                                .title(errorType.getTitle())
                                .message(errMsg)
                                .build());
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResSuccessPattern<T> {
        T data;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResSuccessPagePattern<T> {
        T data;
        Page pagination;
    }

    @Builder
    @Getter
    public static class ResFailPattern {
        int code;
        String title;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String message;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Object errors;
    }

    private int generateCode(int statusCode, int code) {
        return statusCode * 10000 + code;
    }
}