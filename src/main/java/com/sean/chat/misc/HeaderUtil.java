package com.sean.chat.misc;

public class HeaderUtil {
    public static String getAuthorizationHeader(String authorization) {
        if (authorization.startsWith("Bearer")) {
            return authorization;
        }
        return "Bearer " + authorization;
    }
}
