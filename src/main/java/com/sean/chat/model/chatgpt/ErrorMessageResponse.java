package com.sean.chat.model.chatgpt;

import lombok.Data;

@Data
public class ErrorMessageResponse{

    private String errorMessage;


    public ErrorMessageResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessageResponse() {
    }
}
