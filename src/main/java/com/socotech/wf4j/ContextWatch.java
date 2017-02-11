/**
 * SessionWatch.java
 */
package com.socotech.wf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Simple class to help debug session problems. This is normally turned on only for debugging local
 * builds. To do this, edit your wf4j.xml file to have code like this:
 * <p/>
 * <p/>
 * <pre>
 *  &lt;listener&gt;
 *   &lt;listener-class&gt;com.datadepth.lib.ContextWatch&lt;/listener-class&gt;
 *  &lt;/listener&gt;
 * </pre>
 */
public class ContextWatch implements ServletContextListener, ServletContextAttributeListener {

  @Override
  public void contextInitialized(ServletContextEvent event) {
    if (log.isDebugEnabled()) {
      ServletContext context = event.getServletContext();
      log.debug("Context " + context.getServletContextName() + " " + System.identityHashCode(this) + " initialized.");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    if (log.isDebugEnabled()) {
      ServletContext context = event.getServletContext();
      log.debug("Context " + context.getServletContextName() + " " + System.identityHashCode(this) + " destroyed.");
    }
  }

  @Override
  public void attributeAdded(ServletContextAttributeEvent event) {
    if (log.isDebugEnabled()) {
      ServletContext context = event.getServletContext();
      log.debug("Context " + context.getServletContextName() + " " + System.identityHashCode(this) + " attribute added: " + event.getName());
    }
  }

  @Override
  public void attributeRemoved(ServletContextAttributeEvent event) {
    if (log.isDebugEnabled()) {
      ServletContext context = event.getServletContext();
      log.debug("Context " + context.getServletContextName() + " " + System.identityHashCode(this) + " attribute removed: " + event.getName());
    }
  }

  @Override
  public void attributeReplaced(ServletContextAttributeEvent event) {
    if (log.isDebugEnabled()) {
      ServletContext context = event.getServletContext();
      log.debug("Context " + context.getServletContextName() + " " + System.identityHashCode(this) + " attribute replaced: " + event.getName());
    }
  }

  /**
   * logger
   */
  private static final Logger log = Logger.getLogger(ContextWatch.class);
}