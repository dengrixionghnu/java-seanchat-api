package com.sean.chat.aop;

import com.sean.chat.exception.ConversationException;
import com.sean.chat.model.chatgpt.ErrorMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings("unused")
@RestControllerAdvice
public class ConversationExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorMessageResponse> handleConversationException(ConversationException e) {
        return ResponseEntity.status(e.getCode()).body(new ErrorMessageResponse(e.getMessage()));
    }
}
