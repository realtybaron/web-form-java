package com.socotech.wf4j;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class AbstractSimpleFormActionTest {
    @Test
    public void testPost() throws Exception {
        final AbstractSimpleFormAction action = new PostAction();
        final HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        final HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
        final HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        Map<String, Object> params = new HashMap<String, Object>();
        String[] array = {"string0", "string1"};
        params.put("array", array);
        params.put("string", "my string");
        params.put("integer", "1");
        params.put("doubl", "3.14");
        params.put("bool", true);
        params.put("longs", "1");
        params.put("date", "11/19/1973");
        Vector v = new Vector(params.keySet());
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(request.getParameterMap()).andReturn(params);
        EasyMock.expect(request.getParameterNames()).andReturn(v.elements());
        EasyMock.expect(request.getParameterValues("array")).andReturn(array);
        EasyMock.expect(request.getParameterValues("string")).andReturn(new String[]{"my string"});
        EasyMock.expect(request.getParameterValues("integer")).andReturn(new String[]{"1"});
        EasyMock.expect(request.getParameterValues("doubl")).andReturn(new String[]{"3.14"});
        EasyMock.expect(request.getParameterValues("bool")).andReturn(new String[]{"true"});
        EasyMock.expect(request.getParameterValues("longs")).andReturn(new String[]{"1"});
        EasyMock.expect(request.getParameterValues("date")).andReturn(new String[]{"11/19/1973"});
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.replay(request);
        action.execute(request, response);
    }

    @Test
    public void testPostWithValidation() throws Exception {
        final AbstractSimpleFormAction action = new ValidatedAction();
        final HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        final HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
        final HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(request.getParameterMap()).andReturn(Collections.emptyMap());
        EasyMock.expect(request.getParameterNames()).andReturn(new Vector().elements());
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.replay(request);
        action.execute(request, response);
    }

    @Test
    public void testNullValues() throws Exception {
        final AbstractSimpleFormAction action = new NullifyAction();
        final HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        final HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
        final HttpServletResponse response = EasyMock.createNiceMock(HttpServletResponse.class);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("string", null);
        params.put("integer", null);
        params.put("doubl", null);
        params.put("bool", null);
        params.put("longs", null);
        params.put("date", null);
        Vector v = new Vector(params.keySet());
        EasyMock.expect(request.getMethod()).andReturn("post");
        EasyMock.expect(request.getContentType()).andReturn("application/x-www-form-urlencoded");
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.expect(request.getParameterMap()).andReturn(params);
        EasyMock.expect(request.getParameterNames()).andReturn(v.elements());
        EasyMock.expect(request.getParameterValues("string")).andReturn(new String[]{null});
        EasyMock.expect(request.getParameterValues("integer")).andReturn(new String[]{null});
        EasyMock.expect(request.getParameterValues("doubl")).andReturn(new String[]{null});
        EasyMock.expect(request.getParameterValues("bool")).andReturn(new String[]{null});
        EasyMock.expect(request.getParameterValues("longs")).andReturn(new String[]{null});
        EasyMock.expect(request.getParameterValues("date")).andReturn(new String[]{null});
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.replay(request);
        action.execute(request, response);
    }

    @Form(name = "testForm",
            formClass = TestForm.class,
            binders = {@FormBinder(property = "date", editorClass = DatePropertyEditor.class)})
    private class PostAction extends AbstractSimpleFormAction {
        protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws IOException, ServletException {
            TestForm form = (TestForm) o;
            List<String> array = Arrays.asList(form.getArray());
            assertTrue("Unable to extract array element #1 from request", array.contains("string0"));
            assertTrue("Unable to extract array element #2 from request", array.contains("string1"));
            Assert.assertEquals("Unable to extract String from request", "my string", form.getString());
            Assert.assertEquals("Unable to extract int from request", 1, form.getInteger());
            Assert.assertEquals("Unable to extract double from request", 3.14, form.getDoubl(), 0);
            Assert.assertEquals("Unable to extract boolean from request", true, form.isBool());
            Assert.assertEquals("Unable to extract Long from request", 1, form.getLongs().longValue());
            try {
                Assert.assertEquals("Unable to extract Date from request", DateManager.humanStringToDate("11/19/1973"), form.getDate());
            } catch (ParseException e) {
                fail(e.getMessage());
            }
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
            // noop
        }

        @Override
        protected String getFormView(HttpServletRequest request, Object o) {
            return null;
        }

        @Override
        protected String getSuccessView(HttpServletRequest request, Object o) throws Exception {
            return null;
        }

        @Override
        protected void showForm(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws Exception {
            assertTrue("Errors is not empty", errors.isEmpty());
        }
    }

    @Form(name = "testForm",
            formClass = TestForm.class,
            binders = {@FormBinder(property = "date", editorClass = DatePropertyEditor.class)})
    private class NullifyAction extends PostAction {
        @Override
        protected Object getFormObject(HttpServletRequest req) throws Exception {
            TestForm form = TestForm.class.cast(super.getFormObject(req));
            form.setBool(true);
            form.setDate(new Date());
            form.setDoubl(3.14);
            form.setInteger(100);
            form.setString("foo");
            form.setLongs(100L);
            return form;
        }

        @Override
        protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws IOException, ServletException {
            TestForm form = (TestForm) o;
            Assert.assertEquals("String was not nullified", null, form.getString());
            Assert.assertEquals("int was not nullified", 0, form.getInteger());
            Assert.assertEquals("double was not nullified", 0.0, form.getDoubl(), 0);
            Assert.assertEquals("boolean was not nullified", false, form.isBool());
            Assert.assertEquals("Long was not nullified", null, form.getLongs());
            Assert.assertEquals("Date was not nulllified", null, form.getDate());
        }
    }

    @Form(name = "testForm", formClass = TestForm.class, validatorClass = TestFormValidator.class)
    private class ValidatedAction extends PostAction {
        @Override
        protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response, Object o, FormErrors errors) throws IOException, ServletException {
            // noop
        }
    }
}