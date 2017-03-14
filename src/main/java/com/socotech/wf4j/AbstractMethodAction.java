package com.socotech.wf4j;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 3/14/17
 * Time: 8:21 AM
 */
public abstract class AbstractMethodAction extends AbstractAction {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (req.getMethod().equalsIgnoreCase("get")) {
            this.get(req, res);
        } else if (req.getMethod().equalsIgnoreCase("put")) {
            this.put(req, res);
        } else if (req.getMethod().equalsIgnoreCase("post")) {
            this.post(req, res);
        } else if (req.getMethod().equalsIgnoreCase("delete")) {
            this.delete(req, res);
        }
    }

    public abstract void get(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

    public abstract void put(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

    public abstract void post(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;

    public abstract void delete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;
}
