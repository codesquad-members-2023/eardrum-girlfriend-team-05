package com.codesquad.controlG.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum ChatException implements CustomException {

    INVALID_CHAT_ROOM_ID(HttpStatus.BAD_REQUEST, "찾을수 없는 채팅방 아이디 입니다."),
    PARTNER_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "매칭 상대를 찾지 못했습니다."),
    INVALID_CHAT_MEMBER(HttpStatus.BAD_REQUEST, "유효한 채팅방 접속 정보가 아닙니다."),
    MATCHING_NOT_FOUND(HttpStatus.BAD_REQUEST, "매칭된 상대가 아닙니다.");

    private final HttpStatus status;
    private final String message;

    ChatException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    @Override
    public String getErrorMessage() {
        return message;
    }

    @Override
    public String getName() {
        return name();
    }

}
