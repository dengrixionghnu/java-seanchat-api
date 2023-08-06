package com.sean.chat.aop;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class CheckHeaderInterceptor implements HandlerInterceptor {

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println((request.getRequestURI()));

        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            System.out.println(("No access token"));
            return false;
        }

        return true;
    }
}
