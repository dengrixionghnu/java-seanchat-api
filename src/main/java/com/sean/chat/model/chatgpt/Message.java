package com.sean.chat.model.chatgpt;

import lombok.Data;

@Data
public class Message {
    private Author author;
    private Content content;
    private String id;
}
