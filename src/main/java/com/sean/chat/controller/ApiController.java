package com.sean.chat.controller;
import com.sean.chat.misc.Constant;
import com.sean.chat.model.api.ChatCompletionsRequest;
import com.sean.chat.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
public class ApiController {
    @Autowired
    private  ApiService apiService;


    @PostMapping(value = Constant.API_CHAT_COMPLETIONS)
    public List<String> chatCompletions(
            @RequestHeader String authorization,
            @RequestBody ChatCompletionsRequest chatCompletionsRequest) {
        return apiService.chatCompletions(authorization, chatCompletionsRequest);
    }

    @GetMapping(Constant.API_CHECK_CREDIT_GRANTS)
    public String checkCreditGrants(@RequestHeader String authorization) {
        return apiService.checkCreditGrants(authorization);
    }
}
