package com.socotech.wf4j;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/11/17
 * Time: 4:13 PM
 */
public interface FormAction {
    void execute(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException;
}
