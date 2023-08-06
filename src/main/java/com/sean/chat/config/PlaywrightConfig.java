package com.sean.chat.config;


import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Proxy;
import com.sean.chat.annotation.EnabledOnChatGPT;
import com.sean.chat.exception.CaptchaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static com.sean.chat.enums.ErrorEnum.ACCESS_DENIED;
import static com.sean.chat.misc.Constant.CHATGPT_URL;
import static com.sean.chat.misc.Constant.WELCOME_TEXT;
import static com.sean.chat.misc.PlaywrightUtil.*;


@SuppressWarnings("resource")
@EnabledOnChatGPT
@Configuration
public class PlaywrightConfig {
    @Bean
    BrowserContext browserContext() {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(true);
        String proxy = System.getenv("PROXY");
        if (StringUtils.hasText(proxy)) {
            launchOptions = launchOptions.setProxy(new Proxy(proxy));
        }

        // Chromium can not get cf_cookies in headless mode
        Browser browser = Playwright.create()
                .firefox()
                .launch(launchOptions);

        BrowserContext context = browser.newContext();
        context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined});");
        return context;
    }

    @Bean
    Page apiPage(BrowserContext context) {
        Page apiPage = context.newPage();
        apiPage.navigate(CHATGPT_URL);
        apiPage.waitForLoadState(LoadState.NETWORKIDLE);
        apiPage.frames().forEach(frame -> frame.waitForLoadState(LoadState.NETWORKIDLE));

        if (isReady(apiPage)) {
            System.out.println(WELCOME_TEXT);
            apiPage.evaluate("window.conversationMap = new Map();");
            return apiPage;
        }

        if (isAccessDenied(apiPage)) {
            throw new CaptchaException(ACCESS_DENIED);
        }

        return handleCaptcha(apiPage);
    }

    @Bean
    Page refreshPage(BrowserContext context) {
        Page refreshPage = context.newPage();
        refreshPage.navigate(CHATGPT_URL);
        refreshPage.waitForLoadState(LoadState.NETWORKIDLE);

        return refreshPage;
    }
}
