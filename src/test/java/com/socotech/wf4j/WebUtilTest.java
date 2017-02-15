package com.socotech.wf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;

public class WebUtilTest extends TestCase {
    public void testGetOrCreateSessionAttribute() {
        final HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        final HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
        // expectations
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(session.getAttribute("foo")).andReturn("bar");
        // replay
        EasyMock.replay(request);
        EasyMock.replay(session);
        assertEquals("bar", WebUtil.getOrCreateSessionAttribute(request, "foo", null));
    }
}

