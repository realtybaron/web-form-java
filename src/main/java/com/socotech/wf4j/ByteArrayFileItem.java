package com.socotech.wf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.google.common.io.Closeables;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/26/17
 * Time: 11:00 AM
 */
public class ByteArrayFileItem implements FileItem {
    private String fileName;
    private String fieldName;
    private String contentType;
    private boolean formField;
    private FileItemHeaders headers;
    private ByteArrayOutputStream bytes;

    public ByteArrayFileItem(String fieldName, String contentType, boolean formField, String fileName) {
        this.fileName = fileName;
        this.fieldName = fieldName;
        this.formField = formField;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public long getSize() {
        return bytes.toByteArray().length;
    }

    @Override
    public byte[] get() {
        return bytes.toByteArray();
    }

    @Override
    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(bytes.toByteArray(), encoding);
    }

    @Override
    public String getString() {
        return new String(bytes.toByteArray());
    }

    @Override
    public void write(File file) throws Exception {
        // noop
    }

    @Override
    public void delete() {
        try {
            Closeables.close(this.bytes, true);
        } catch (IOException e) {
            // noop
        }
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setFieldName(String name) {
        this.fieldName = name;
    }

    @Override
    public boolean isFormField() {
        return this.formField;
    }

    @Override
    public void setFormField(boolean b) {
        this.formField = b;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        this.bytes = new ByteArrayOutputStream();
        return this.bytes;
    }

    @Override
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        this.headers = headers;
    }
}
