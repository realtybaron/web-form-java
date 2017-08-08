package com.socotech.wf4j;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 4/12/17
 * Time: 6:50 AM
 */
public class SessionDebugger implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        log.debug("Session attribute {} added: {}", se.getName(), se.getValue());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        log.debug("Session attribute {} removed: {}", se.getName(), se.getValue());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        log.debug("Session attribute {} replaced: {}", se.getName(), se.getValue());
    }

    /**
     * <p/> A logging category for each action. </p>
     */
    private static final Logger log = LoggerFactory.getLogger(SessionDebugger.class);
}
