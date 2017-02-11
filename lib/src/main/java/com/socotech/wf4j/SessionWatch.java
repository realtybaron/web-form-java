/**
 * SessionWatch.java
 */
package com.socotech.wf4j;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Simple class to help debug session problems. This is normally turned on only for debugging local
 * builds. To do this, edit your wf4j.xml file to have code like this:
 * <p/>
 * <pre>
 *  &lt;listener&gt;
 *   &lt;listener-class&gt;com.datadepth.lib.SessionWatch&lt;/listener-class&gt;
 *  &lt;/listener&gt;
 * </pre>
 */
public class SessionWatch implements HttpSessionListener, HttpSessionAttributeListener {
  private Logger log = Logger.getLogger(SessionWatch.class);

  public void sessionCreated(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    log.debug("Session " + session.getId() + " created; container will keep open " + session.getMaxInactiveInterval() + " seconds.");
  }

  public void sessionDestroyed(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    log.debug("Session " + session.getId() + " destroyed.");
  }

  public void attributeAdded(HttpSessionBindingEvent event) {
    HttpSession session = event.getSession();
    log.debug("Session " + session.getId() + " attribute added: " + event.getName() + "=" + event.getValue().toString());
  }

  public void attributeRemoved(HttpSessionBindingEvent event) {
    HttpSession session = event.getSession();
    log.debug("Session " + session.getId() + " attribute removed: " + event.getName());
  }

  public void attributeReplaced(HttpSessionBindingEvent event) {
    HttpSession session = event.getSession();
    log.debug("Session " + session.getId() + " attribute replaced: " + event.getName() + "=" + event.getValue().toString());
  }
}