package com.sean.chat.service;

import com.sean.chat.model.api.ChatCompletionsRequest;

import java.util.List;

public interface ApiService {
    List<String> chatCompletions(String authorization, ChatCompletionsRequest chatCompletionsRequest);

    String checkCreditGrants(String authorization);
}
