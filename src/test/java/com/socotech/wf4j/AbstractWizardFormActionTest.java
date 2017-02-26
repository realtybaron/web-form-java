package com.socotech.wf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AbstractWizardFormActionTest {
    final HttpSession session = EasyMock.createNiceMock(HttpSession.class);
    final HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
    final HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);

    @Test
    public void testFirst() throws Exception {
        // expectations
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap()).anyTimes();
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements()).anyTimes();
        // replay
        EasyMock.replay(session);
        EasyMock.replay(request);
        // action
        MultiPageAction action = new MultiPageAction();
        action.execute(request, response);
        assertEquals("Wrong page", 0, action.targetPage);
    }

    @Test
    public void testNext() throws Exception {
        // expectations
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(session.getAttribute("testForm")).andReturn(null).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap()).anyTimes();
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements()).anyTimes();
        // replay
        EasyMock.replay(session);
        EasyMock.replay(request);
        // action
        MultiPageAction action = new MultiPageAction();
        action.execute(request, response);
        assertEquals("Wrong page", 1, action.targetPage);
    }

    @Test
    public void testTarget() throws Exception {
        // expectations
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(session.getAttribute("testForm")).andReturn(null).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap()).anyTimes();
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements()).anyTimes();
        EasyMock.replay(session);
        EasyMock.replay(request);
        // action
        MultiPageAction action = new MultiPageAction();
        action.execute(request, response);
        assertEquals("Wrong page", 1, action.targetPage);
    }

    @Test
    public void testBack() throws Exception {
        // expectations
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(session.getAttribute("testForm")).andReturn(null).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap()).anyTimes();
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements()).anyTimes();
        // replay
        EasyMock.replay(session);
        EasyMock.replay(request);
        // action
        MultiPageAction action = new MultiPageAction();
        action.execute(request, response);
        assertEquals("Wrong page", 0, action.targetPage);
    }

    @Test
    public void testFinish() throws Exception {
        // expectations
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(session.getAttribute("testForm")).andReturn(null).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(new HashMap()).anyTimes();
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements()).anyTimes();
        // replay
        EasyMock.replay(session);
        EasyMock.replay(request);
        // action
        MultiPageAction action = new MultiPageAction();
        action.execute(request, response);
        assertTrue("Finish not called when expected", action.finishCalled);
    }

    @Form(name = "testForm", formClass = TestWizardForm.class, sessionForm = false)
    public static final class MultiPageAction extends AbstractWizardFormAction {
        int targetPage;
        String forwardToPage;
        boolean finishCalled;
        boolean executePageCalled;
        boolean validatePageCalled;
        String[] pages = new String[]{"page1.jsp", "page2.jsp"};

        @Override
        protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
            this.forwardToPage = page;
        }

        @Override
        protected String getView(HttpServletRequest request, int page) {
            this.targetPage = page;
            return this.pages[page];
        }

        @Override
        protected Map<String, Object> getAuxiliaryData(HttpServletRequest request, Object o, int page) throws Exception {
            return Collections.emptyMap();
        }

        @Override
        protected Set<Pair> getOffPageParameters(HttpServletRequest request, Object o, int page) throws Exception {
            return Collections.emptySet();
        }

        @Override
        protected void validatePage(HttpServletRequest request, Object command, FormErrors errors, int page) throws Exception {
            this.validatePageCalled = true;
        }

        @Override
        protected void executePage(HttpServletRequest request, HttpServletResponse response, Object o, int page) throws IOException, ServletException {
            this.executePageCalled = true;
        }

        @Override
        protected void exit(Object o, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        }

        @Override
        protected void cancel(Object o, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        }

        @Override
        protected void finish(Object o, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            this.finishCalled = true;
        }
    }
}
