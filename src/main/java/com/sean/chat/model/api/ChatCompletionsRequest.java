package com.sean.chat.model.api;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionsRequest {

   private  String model;
   private  List<Message> messages;
   private  boolean stream;
}
