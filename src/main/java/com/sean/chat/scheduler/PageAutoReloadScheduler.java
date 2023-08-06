package com.sean.chat.scheduler;

import com.microsoft.playwright.Page;
import com.sean.chat.annotation.EnabledOnChatGPT;
import com.sean.chat.misc.PlaywrightUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnabledOnChatGPT
@Component
public class PageAutoReloadScheduler {
    private final Page refreshPage;

    public PageAutoReloadScheduler(Page refreshPage) {
        this.refreshPage = refreshPage;
    }

    // auto reload every 1 minutes
    @Scheduled(fixedRate = 10*60_000, initialDelay = 10*60_000)
    public void reload() {
        PlaywrightUtil.tryToReload(refreshPage);
    }
}
