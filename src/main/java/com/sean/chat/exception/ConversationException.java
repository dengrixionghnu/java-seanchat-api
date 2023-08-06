package com.sean.chat.exception;

import com.sean.chat.enums.ErrorEnum;
import lombok.Getter;

public class ConversationException extends RuntimeException {
    @Getter
    private final int code;

    public ConversationException(ErrorEnum errorEnum) {
        super(errorEnum.message);

        this.code = errorEnum.code;
    }

    public ConversationException(int code, String message) {
        super(message);

        this.code = code;
    }
}
