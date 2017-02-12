package com.socotech.wf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.servlet.UriPatternMatcher;
import com.google.inject.servlet.UriPatternType;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/11/17
 * Time: 4:01 PM
 */
public class WF4JController extends HttpServlet {
    private Injector injector;
    private List<Action> actions;
    private Map<Class, FormAction> actionMap;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.actions = Lists.newArrayList();
        this.injector = (Injector) config.getServletContext().getAttribute(Injector.class.getName());
        this.actionMap = new ConcurrentHashMap<>();

        URL url = ClasspathHelper.forWebInfClasses(config.getServletContext());
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(url));
        Set<Class<?>> actionClasses = reflections.getTypesAnnotatedWith(Action.class);
        for (Class<?> actionClass : actionClasses) {
            Collections.addAll(this.actions, actionClass.getAnnotation(Actions.class).actions());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String actionURL = Requests.getRequestUri(req);
        for (Action action : actions) {
            UriPatternMatcher matcher = UriPatternType.get(UriPatternType.SERVLET, action.path());
            if (matcher != null && matcher.matches(actionURL)) {
                FormAction formAction = actionMap.get(action.type());
                if (formAction == null) {
                    try {
                        formAction = (FormAction) injector.getInstance(action.type());
                        actionMap.put(action.type(), formAction);
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
                formAction.execute(req, resp);
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
