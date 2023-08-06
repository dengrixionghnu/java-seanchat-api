package com.sean.chat.config;

import com.sean.chat.aop.CheckHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final CheckHeaderInterceptor checkHeaderInterceptor;

    public WebMvcConfig(CheckHeaderInterceptor checkHeaderInterceptor) {
        this.checkHeaderInterceptor = checkHeaderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkHeaderInterceptor).addPathPatterns("/**");
    }
}
