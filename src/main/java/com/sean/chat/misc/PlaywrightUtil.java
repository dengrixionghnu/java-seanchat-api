package com.sean.chat.misc;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import lombok.SneakyThrows;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.sean.chat.misc.Constant.WELCOME_TEXT;


public class PlaywrightUtil {
    private static final int INTERVAL = 1;
    private static int totalClickCaptchaCount = 0;

    private static boolean firstTime = true;

    public static boolean isAccessDenied(Page page) {
        try {
            ElementHandle element = page.waitForSelector(".cf-error-details", new Page.WaitForSelectorOptions().setTimeout(2_000));
            System.out.println(element.textContent());
            return true;
        } catch (PlaywrightException e) {
            page.waitForTimeout(1_000);
        }
        return false;
    }

    public static boolean isReady(Page page) {
        page.waitForLoadState(LoadState.NETWORKIDLE);
        return page.title().contains("ChatGPT");
    }

    @SneakyThrows
    public static boolean isCaptchaClicked(Page page) {
        try {
            String title = page.title();
            if (isNotBlank(title) || title.equals("Just a moment...")) {
                tryToClickCaptcha(page);
            }
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    @SneakyThrows
    private static void tryToClickCaptcha(Page page) {
        Locator iframe = page.getByTitle("Widget containing a Cloudflare security challenge");
        page.waitForCondition(iframe::isVisible);

        page.frames().stream()
                .filter(frame -> frame.url().startsWith("https://challenges.cloudflare.com"))
                .findFirst()
                .ifPresentOrElse(PlaywrightUtil::clickCheckBox, () -> {
                    iframe.click();
                    totalClickCaptchaCount++;
                });

        try {
            page.waitForCondition(() -> page.context().cookies().stream().anyMatch(cookie -> cookie.name.equals("cf_clearance")), new Page.WaitForConditionOptions().setTimeout(5_000));
        } catch (TimeoutError error) {
            page.reload();
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.frames().forEach(frame -> frame.waitForLoadState(LoadState.NETWORKIDLE));
            handleCaptcha(page);
        }
    }

    @SneakyThrows
    private static void clickCheckBox(Frame frame) {
        Locator checkbox = frame.getByRole(AriaRole.CHECKBOX);
        while (!checkbox.isVisible()) {
            TimeUnit.SECONDS.sleep(INTERVAL);
        }
        checkbox.check();
        totalClickCaptchaCount++;
    }

    public static Page handleCaptcha(Page page) {
        if (isCaptchaClicked(page) && isReady(page)) {
            if (totalClickCaptchaCount != 0) {
                System.out.println("Total click " + totalClickCaptchaCount + " times to pass captcha");
                totalClickCaptchaCount = 0;
            }
            if (firstTime) {
                System.out.println(WELCOME_TEXT);
                firstTime = false;
                page.evaluate("window.conversationMap = new Map();");
            }
            return page;
        } else {
            return handleCaptcha(page);
        }
    }

    public static void tryToReload(Page page) {
        try {
            page.reload();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            if (!isReady(page)) {
                handleCaptcha(page);
            }
        } catch (Exception ignored) {
        }
    }


    private static boolean isNotBlank(String str){
        return  Objects.nonNull(str)&&str.length()>0;
    }
}
