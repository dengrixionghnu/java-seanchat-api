package com.sean.chat.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeedbackRequest{

        @JsonProperty("message_id")
      private   String messageId;
        @JsonProperty("conversation_id")
     private    String conversationId;
      private   String rating;
}
