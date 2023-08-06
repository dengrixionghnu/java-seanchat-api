package com.sean.chat.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.sean.chat.annotation.EnabledOnChatGPT;
import com.sean.chat.enums.ErrorEnum;
import com.sean.chat.exception.ConversationException;
import com.sean.chat.misc.Constant;
import com.sean.chat.misc.PlaywrightUtil;
import com.sean.chat.model.chatgpt.*;
import com.sean.chat.service.ChatGPTService;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.sean.chat.misc.HeaderUtil.getAuthorizationHeader;
import static com.sean.chat.misc.JsUtil.*;


@EnabledOnChatGPT
@Service
public class ChatGPTServiceImpl implements ChatGPTService {
    private final Page apiPage;
    private final Page refreshPage;

    private final ObjectMapper objectMapper;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ChatGPTServiceImpl(Page apiPage, Page refreshPage, ObjectMapper objectMapper) {
        this.apiPage = apiPage;
        this.refreshPage = refreshPage;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public ResponseEntity<String> getConversations(String accessToken, int offset, int limit) {
         String responseText = (String) apiPage.evaluate(
                getGetScript(
                        String.format(Constant.GET_CONVERSATIONS_URL, offset, limit),
                        accessToken,
                        Constant.ERROR_MESSAGE_GET_CONVERSATIONS
                )
        );
        if (Constant.ERROR_MESSAGE_GET_CONVERSATIONS.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return getConversations(accessToken, offset, limit);
        }

        return ResponseEntity.ok(responseText);
    }

    @Override
    public List<String> startConversation(String accessToken, ConversationRequest conversationRequest) {
        List<String> result = new ArrayList<>();
        try {
            // add support for old chatgpt api
            Message message = conversationRequest.getMessages().get(0);
            Author author = message.getAuthor();
            if (author == null || author.getRole() == null) {
                author = new Author();
                author.setRole("user");
                message.setAuthor(author);
            }
            Integer timezoneOffsetMin = conversationRequest.getTimezoneOffsetMin();
            if (timezoneOffsetMin == null) {
                conversationRequest.setTimezoneOffsetMin(0);
            }
            String variantPurpose = conversationRequest.getVariantPurpose();
            if (variantPurpose == null) {
                conversationRequest.setVariantPurpose("none");
            }
            String oldContentToResponse = "";
            sendConversationRequest(accessToken, conversationRequest, oldContentToResponse,result);
        } catch (Exception e) {
            System.out.println(("Start conversation exception: " + e.getLocalizedMessage()));
        }
        return result;

    }

    @SneakyThrows
    private void sendConversationRequest(String accessToken, ConversationRequest conversationRequest, String oldContentToResponse, List<String> result) {

        String requestBody = objectMapper.writeValueAsString(conversationRequest);
        String messageId = conversationRequest.getMessages().get(0).getId();
        apiPage.evaluate(getPostScriptForStartConversation(Constant.START_CONVERSATIONS_URL, getAuthorizationHeader(accessToken), requestBody, messageId));

        // prevent handle multiple times
        String temp = "";
        ConversationResponse conversationResponse = null;
        boolean maxTokens = false;
        while (true) {
            String conversationResponseData;
            try {
                conversationResponseData = (String) apiPage.evaluate(String.format("() => conversationMap.get('%s')", messageId));
                if (conversationResponseData == null || isNotBlank(conversationResponseData)) {
                    continue;
                }
            } catch (PlaywrightException ignored) {
                continue;
            }

            if (conversationResponseData.charAt(0) == '4' || conversationResponseData.charAt(0) == '5') {
                Integer statusCode = Integer.parseInt(conversationResponseData.substring(0, 3));
                if (statusCode == HttpStatus.FORBIDDEN.value()) {
                    refreshPage.reload();
                }
                throw new RuntimeException("String error");
            }
            if (conversationResponseData.charAt(0) == '!') {
                result.add(conversationResponseData.substring(1));
                break;
            }

            if (Objects.nonNull(temp) && temp.length()>0) {
                if (temp.equals(conversationResponseData)) {
                    continue;
                }
            }
            temp = conversationResponseData;

            try {
                conversationResponse = objectMapper.readValue(conversationResponseData, ConversationResponse.class);
            } catch (JsonParseException e) {
                continue;
            }

            ConversationResponseMessage message = conversationResponse.getConversationResponseMessage();
            if (isNotBlank(oldContentToResponse)) {
                result.add(conversationResponseData);
            } else {
                List<String> parts = message.getContent().getParts();
                parts.set(0, oldContentToResponse + parts.get(0));
                result.add(objectMapper.writeValueAsString(conversationResponse));
            }

            FinishDetails finishDetails = message.getMetadata().getFinishDetails();
            if (finishDetails != null && "max_tokens".equals(finishDetails.getType())) {
                String continueText = conversationRequest.getContinueText();
                if (StringUtils.hasText(continueText)) {
                    maxTokens = true;
                    oldContentToResponse = message.getContent().getParts().get(0);
                }
                break;
            }

            boolean endTurn = message.isEndTurn();
            if (endTurn) {
                break;
            }
        }
        if (maxTokens && StringUtils.hasText(conversationRequest.getContinueText())) {
            TimeUnit.SECONDS.sleep(1);

            ConversationRequest newRequest = newConversationRequest(conversationRequest, conversationResponse);
            sendConversationRequest(accessToken, newRequest, oldContentToResponse, result);
        }
        apiPage.evaluate(String.format("conversationMap.delete('%s');", messageId));
    }

    private static ConversationRequest newConversationRequest(ConversationRequest conversationRequest, ConversationResponse conversationResponse) {
        Author author = new Author();
        author.setRole("user");
        Content content = new Content();
        content.setContentType("text");
        content.setParts(Arrays.asList(conversationRequest.getContinueText()));
        Message message = new Message();
        message.setAuthor(author);
        message.setContent(content);
        message.setId(UUID.randomUUID().toString());
        return new ConversationRequest(
                conversationRequest.getAction(),
                Arrays.asList(message),
                conversationRequest.getModel(),
                conversationResponse.getConversationResponseMessage().getId(),
                conversationResponse.getConversationId(),
                conversationRequest.getTimezoneOffsetMin(),
                conversationRequest.getVariantPurpose(),
                conversationRequest.getContinueText()
        );
    }

    @SneakyThrows
    @Override
    public ResponseEntity<String> genConversationTitle(
            String accessToken,
            String conversationId,
            GenerateTitleRequest generateTitleRequest) {
        String jsonBody = objectMapper.writeValueAsString(generateTitleRequest);
        String responseText = (String) apiPage.evaluate(
                getPostScript(
                        String.format(Constant.GENERATE_TITLE_URL, conversationId),
                        accessToken,
                        jsonBody,
                        Constant.ERROR_MESSAGE_GENERATE_TITLE
                )
        );
        if (Constant.ERROR_MESSAGE_GENERATE_TITLE.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            throw new ConversationException(ErrorEnum.GENERATE_TITLE_ERROR);
        }
        return ResponseEntity.ok(responseText);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<String> getConversationContent(String accessToken, String conversationId) {
        String responseText = (String) apiPage.evaluate(
                getGetScript(
                        String.format(Constant.GET_CONVERSATION_CONTENT_URL, conversationId),
                        accessToken,
                        Constant.ERROR_MESSAGE_GET_CONTENT
                )
        );
        if (Constant.ERROR_MESSAGE_GET_CONTENT.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            throw new ConversationException(ErrorEnum.GET_CONTENT_ERROR);
        }
        return ResponseEntity.ok(responseText);
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Boolean> updateConversation(String accessToken, String conversationId, UpdateConversationRequest updateConversationRequest) {
        String jsonBody = objectMapper.writeValueAsString(updateConversationRequest);
        String responseText = (String) apiPage.evaluate(
                getPatchScript(
                        String.format(Constant.UPDATE_CONVERSATION_URL, conversationId),
                        accessToken,
                        jsonBody,
                        Constant.ERROR_MESSAGE_UPDATE_CONVERSATION
                )
        );
        if (Constant.ERROR_MESSAGE_UPDATE_CONVERSATION.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return updateConversation(accessToken, conversationId, updateConversationRequest);
        }

        return ResponseEntity.ok((Boolean) objectMapper.readValue(responseText, Map.class).get("success"));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Boolean> clearConversations(String accessToken, UpdateConversationRequest updateConversationRequest) {
        String jsonBody = objectMapper.writeValueAsString(updateConversationRequest);
        String responseText = (String) apiPage.evaluate(
                getPatchScript(
                        Constant.CLEAR_CONVERSATIONS_URL,
                        accessToken,
                        jsonBody,
                        Constant.ERROR_MESSAGE_CLEAR_CONVERSATIONS
                )
        );
        if (Constant.ERROR_MESSAGE_CLEAR_CONVERSATIONS.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return clearConversations(accessToken, updateConversationRequest);
        }

        return ResponseEntity.ok((Boolean) objectMapper.readValue(responseText, Map.class).get("success"));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<String> feedbackMessage(String accessToken, FeedbackRequest feedbackRequest) {
        String jsonBody = objectMapper.writeValueAsString(feedbackRequest);
        String responseText = (String) apiPage.evaluate(
                getPostScript(
                        Constant.FEEDBACK_MESSAGE_URL,
                        accessToken,
                        jsonBody,
                        Constant.ERROR_MESSAGE_FEEDBACK_MESSAGE
                )
        );
        if (Constant.ERROR_MESSAGE_FEEDBACK_MESSAGE.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return feedbackMessage(accessToken, feedbackRequest);
        }

        return ResponseEntity.ok((String) objectMapper.readValue(responseText, Map.class).get("rating"));
    }

    @Override
    public ResponseEntity<String> getModels(String accessToken) {
        String responseText = (String) apiPage.evaluate(
                getGetScript(
                        String.format(Constant.GET_MODELS_URL),
                        accessToken,
                        Constant.ERROR_MESSAGE_GET_MODELS
                )
        );
        if (Constant.ERROR_MESSAGE_GET_MODELS.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return getModels(accessToken);
        }

        return ResponseEntity.ok(responseText);
    }

    @Override
    public ResponseEntity<String> checkAccount(String accessToken) {
        String responseText = (String) apiPage.evaluate(
                getGetScript(
                        String.format(Constant.CHECK_ACCOUNT_URL),
                        accessToken,
                        Constant.ERROR_MESSAGE_CHECK_ACCOUNT
                )
        );
        if (Constant.ERROR_MESSAGE_CHECK_ACCOUNT.equals(responseText)) {
            PlaywrightUtil.tryToReload(refreshPage);
            return checkAccount(accessToken);
        }

        return ResponseEntity.ok(responseText);
    }


    private boolean isNotBlank(String str){
       return  Objects.nonNull(str)&&str.length()>0;
    }
}
