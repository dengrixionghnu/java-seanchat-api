package com.sean.chat.misc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

@Slf4j
public class LogUtil {
    public static void info(String msg) {
        log.info(AnsiOutput.toString(AnsiStyle.BOLD, AnsiColor.GREEN, msg, AnsiColor.DEFAULT));
    }

    public static void warn(String msg) {
        log.warn(AnsiOutput.toString(AnsiStyle.BOLD, AnsiColor.YELLOW, msg, AnsiColor.DEFAULT));
    }

    public static void error(String msg) {
        log.error(AnsiOutput.toString(AnsiStyle.BOLD, AnsiColor.RED, msg, AnsiColor.DEFAULT));
    }
}
