package com.socotech.wf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/11/17
 * Time: 4:01 PM
 */
public class WF4JController extends HttpServlet {
    private Injector injector;
    private List<WebAction> actions;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // init members
        this.actions = Lists.newArrayList();
        this.injector = (Injector) config.getServletContext().getAttribute(Injector.class.getName());
        // find annotations
        URL url = ClasspathHelper.forWebInfClasses(config.getServletContext());
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(url));
        Set<Class<?>> actionClasses = reflections.getTypesAnnotatedWith(WebActions.class);
        for (Class<?> actionClass : actionClasses) {
            Collections.addAll(this.actions, actionClass.getAnnotation(WebActions.class).actions());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // init vars
        String actionURL = Requests.getRequestUri(req);
        WebExecutable executable = null;
        // find web action
        for (WebAction action : this.actions) {
            UriPatternMatcher matcher = UriPatternType.get(UriPatternType.SERVLET, action.path());
            if (matcher != null && matcher.matches(actionURL)) {
                try {
                    executable = (WebExecutable) injector.getInstance(action.type());
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        // found an action?
        if (executable != null) {
            executable.execute(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * <p/> A logging category for each action. </p>
     */
    private static final Logger log = LoggerFactory.getLogger(WF4JController.class);
}
