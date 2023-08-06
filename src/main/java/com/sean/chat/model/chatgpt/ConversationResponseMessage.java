package com.sean.chat.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationResponseMessage{

       private String id;
       private Content content;
        @JsonProperty("end_turn")
       private boolean endTurn;
       private Metadata metadata;
}
