package com.sean.chat.service.impl;
import com.google.gson.Gson;
import com.sean.chat.misc.Constant;
import com.sean.chat.model.api.ChatCompletionDTO;
import com.sean.chat.model.api.ChatCompletionsRequest;
import com.sean.chat.service.ApiService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.sean.chat.misc.HeaderUtil.getAuthorizationHeader;


@Service
public class ApiServiceImpl implements ApiService {

    private RestTemplate restTemplate =  new RestTemplate();

    Gson gson = new Gson();


    @SneakyThrows
    @Override
    public List<String> chatCompletions(String authorization, ChatCompletionsRequest chatCompletionsRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader(authorization));
        HttpEntity<ChatCompletionsRequest> requestEntity = new HttpEntity<>(chatCompletionsRequest, headers);
        String resultJson= restTemplate.exchange(Constant.API_URL+"/"+Constant.API_CHAT_COMPLETIONS, HttpMethod.POST,requestEntity,
                String.class
        ).getBody();
        System.out.println(resultJson);
        ChatCompletionDTO result = gson.fromJson(resultJson,ChatCompletionDTO.class);
        if(Objects.isNull(result) || Objects.isNull(result.getChoices()) || result.getChoices().isEmpty()){
            return new ArrayList<>();
        }

        return Arrays.asList(result.getChoices().get(0).getMessage().getContent());

    }

    @Override
    public String checkCreditGrants(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthorizationHeader(authorization));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(Constant.API_URL +"/"+ Constant.API_CHECK_CREDIT_GRANTS, HttpMethod.GET,
                httpEntity,
                String.class
        );
        return response.getBody();

    }
}
