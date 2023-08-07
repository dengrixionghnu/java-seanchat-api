package com.sean.chat.service.impl;
import com.sean.chat.misc.Constant;
import com.sean.chat.model.api.ChatCompletionsRequest;
import com.sean.chat.service.ApiService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.sean.chat.misc.HeaderUtil.getAuthorizationHeader;


@Service
public class ApiServiceImpl implements ApiService {

    private RestTemplate restTemplate =  new RestTemplate();


    @SneakyThrows
    @Override
    public List<String> chatCompletions(String authorization, ChatCompletionsRequest chatCompletionsRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader(authorization));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        HttpEntity<ChatCompletionsRequest> requestEntity = new HttpEntity<>(chatCompletionsRequest, headers);
        return restTemplate.exchange(Constant.API_URL+"/"+Constant.API_CHAT_COMPLETIONS, HttpMethod.POST,requestEntity,
                new ParameterizedTypeReference<List<String>>() {}
        ).getBody();
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
