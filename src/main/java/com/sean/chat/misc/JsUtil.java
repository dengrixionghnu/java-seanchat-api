package com.sean.chat.misc;

import static com.sean.chat.misc.HeaderUtil.getAuthorizationHeader;

public class JsUtil {
    public static String getGetScript(String url, String accessToken, String errorMessage) {
        return String.format("fetch('%s', {\n" +
                "                    headers: {\n" +
                "                        'Authorization': '%s'\n" +
                "                    }\n" +
                "                })\n" +
                "                .then(response => {\n" +
                "                    if (!response.ok) {\n" +
                "                        throw new Error('%s');\n" +
                "                    }\n" +
                "                    return response.text();\n" +
                "                })\n" +
                "                .catch(err => {\n" +
                "                    return err.message;\n" +
                "                });",url, getAuthorizationHeader(accessToken), errorMessage);
    }

    @SuppressWarnings({"SameParameterValue", "SpellCheckingInspection"})
    public static String getPostScriptForStartConversation(String url, String accessToken, String jsonString, String messageId) {
        return String.format(
                "const getEndTurnMessage = (dataArray) => {\n" +
                        "                    dataArray.pop(); // empty\n" +
                        "                    dataArray.pop(); // data: [DONE]\n" +
                        "                    return '!' + dataArray.pop().substring(6); // endTurn message\n" +
                        "                };\n" +
                        "\n" +
                        "                const xhr = new XMLHttpRequest();\n" +
                        "                xhr.open('POST', '%s');\n" +
                        "                xhr.setRequestHeader('Accept', 'text/event-stream');\n" +
                        "                xhr.setRequestHeader('Authorization', '%s');\n" +
                        "                xhr.setRequestHeader('Content-Type', 'application/json');\n" +
                        "                xhr.onreadystatechange = function() {\n" +
                        "                    switch (xhr.readyState) {\n" +
                        "                        case xhr.LOADING: {\n" +
                        "                            switch (xhr.status) {\n" +
                        "                                case 200: {\n" +
                        "                                    const dataArray = xhr.responseText.substr(xhr.seenBytes).split(\"\\n\\n\");\n" +
                        "                                    dataArray.pop(); // empty string\n" +
                        "                                    if (dataArray.length) {\n" +
                        "                                        let data = dataArray.pop(); // target data\n" +
                        "                                        if (data === 'data: [DONE]') { // this DONE will break the ending handling\n" +
                        "                                            data = getEndTurnMessage(xhr.responseText.split(\"\\n\\n\"));\n" +
                        "                                        } else if (data.startsWith('event')) {\n" +
                        "                                            data = data.substring(49);\n" +
                        "                                        }\n" +
                        "                                        if (data) {\n" +
                        "                                            if (data.startsWith('!')) {\n" +
                        "                                                conversationMap.set('[MESSAGE_!@#_ID]', data);\n" +
                        "                                            } else {\n" +
                        "                                                conversationMap.set('[MESSAGE_!@#_ID]', data.substring(6));\n" +
                        "                                            }\n" +
                        "                                        }\n" +
                        "                                    }\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 401: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + 'Access token has expired.');\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 403: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + 'Something went wrong. If this issue persists please contact us through our help center at help.openai.com.');\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 404: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + JSON.parse(xhr.responseText).detail);\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 413: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + JSON.parse(xhr.responseText).detail.message);\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 422: {\n" +
                        "                                    const detail = JSON.parse(xhr.responseText).detail[0];\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + detail.loc + ' -> ' + detail.msg);\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 429: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + JSON.parse(xhr.responseText).detail);\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                                case 500: {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', xhr.status + 'Unknown error.');\n" +
                        "                                    break;\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                            xhr.seenBytes = xhr.responseText.length;\n" +
                        "                            break;\n" +
                        "                        }\n" +
                        "                        case xhr.DONE:\n" +
                        "                            // keep exception handling\n" +
                        "                            const conversationData = conversationMap.get('[MESSAGE_!@#_ID]');\n" +
                        "                            if (conversationData) {\n" +
                        "                                if (!conversationData.startsWith('4') && !conversationData.startsWith('5')) {\n" +
                        "                                    conversationMap.set('[MESSAGE_!@#_ID]', getEndTurnMessage(xhr.responseText.split(\"\\n\\n\")));\n" +
                        "                                }\n" +
                        "                            }\n" +
                        "                            break;\n" +
                        "                    }\n" +
                        "                };\n" +
                        "                xhr.send(JSON.stringify(%s));"
                ,url, getAuthorizationHeader(accessToken), jsonString.replace("[MESSAGE_!@#_ID]", messageId));
    }

    public static String getPostScript(String url, String accessToken, String jsonBody, String errorMessage) {
        return String.format(
                "fetch('%s', {\n" +
                        "                    method: 'POST',\n" +
                        "                    headers: {\n" +
                        "                        'Authorization': '%s',\n" +
                        "                        'Content-Type': 'application/json'\n" +
                        "                    },\n" +
                        "                    body: JSON.stringify(%s)\n" +
                        "                })\n" +
                        "                .then(response => {\n" +
                        "                    if (!response.ok) {\n" +
                        "                        throw new Error('%s');\n" +
                        "                    }\n" +
                        "                    return response.text();\n" +
                        "                })\n" +
                        "                .catch(err => {\n" +
                        "                    return err.message;\n" +
                        "                });",url, getAuthorizationHeader(accessToken), jsonBody, errorMessage

        );
    }

    public static String getPatchScript(String url, String accessToken, String jsonBody, String errorMessage) {

        return String.format("               fetch('%s', {\n" +
                        "                    method: 'PATCH',\n" +
                        "                    headers: {\n" +
                        "                        'Authorization': '%s',\n" +
                        "                        'Content-Type': 'application/json'\n" +
                        "                    },\n" +
                        "                    body: JSON.stringify(%s)\n" +
                        "                })\n" +
                        "                .then(response => {\n" +
                        "                    if (!response.ok) {\n" +
                        "                        throw new Error('%s');\n" +
                        "                    }\n" +
                        "                    return response.text();\n" +
                        "                })\n" +
                        "                .catch(err => {\n" +
                        "                    return err.message;\n" +
                        "                });",url, getAuthorizationHeader(accessToken), jsonBody, errorMessage);
    }
}
