package org.example.baro.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorDescription implements ErrorCode {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), "U001", "사용자가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "U002", "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "A001", "유효하지 않은 토큰입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "G001", "유효하지 않은 입력입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT.value(), "U003", "이미 가입된 이메일입니다.");

    private final int status;
    private final String code;
    private final String message;
}

