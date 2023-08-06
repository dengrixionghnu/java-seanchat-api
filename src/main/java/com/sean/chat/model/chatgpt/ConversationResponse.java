package com.sean.chat.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationResponse{


        @JsonProperty("message")
        private   ConversationResponseMessage conversationResponseMessage;
        @JsonProperty("conversation_id")
        private    String conversationId;

}
