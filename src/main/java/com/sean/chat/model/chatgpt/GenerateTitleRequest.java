package com.sean.chat.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenerateTitleRequest{

        @JsonProperty("message_id")
      private  String messageId;
      private   String model;
}
