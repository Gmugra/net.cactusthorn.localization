package net.cactusthorn.localization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;

public abstract class WithLoggerTestAncestor {

    protected ListAppender<ILoggingEvent> logAppender;

    protected abstract Logger getLogger();

    @BeforeEach
    protected void addAppender() {
        logAppender = new ListAppender<>();
        logAppender.start();
        getLogger().addAppender(logAppender);
    }

    @AfterEach
    protected void detachAppender() {
        getLogger().detachAppender(logAppender);
        logAppender.stop();
    }

    //@formatter:off
    protected boolean isCauseMessageInLog(Level level, String message) {
        return
            logAppender.list
                .stream()
                .filter(e -> level.equals(e.getLevel()))
                .map(ILoggingEvent::getThrowableProxy)
                .map(IThrowableProxy::getMessage)
                .filter(m -> message.equals(m))
                .findAny()
                .isPresent();
    }
    //@formatter:on

    //@formatter:off
    protected boolean isMessageInLog(Level level, String message) {
        return
            logAppender.list
                .stream()
                .filter(e -> level.equals(e.getLevel()))
                .map(ILoggingEvent::getFormattedMessage)
                .filter(m -> message.equals(m))
                .findAny()
                .isPresent();
    }
    //@formatter:on
}
