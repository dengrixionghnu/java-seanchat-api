 package com.sean.chat.model.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

 @Data
public class Metadata{
          @JsonProperty("finish_details")
          private   FinishDetails finishDetails;
}
