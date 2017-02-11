package com.socotech.wf4j;

/**
 * FormValidator.java
 */
public interface FormValidator<T> {
    void validate(T t, ErrorPacket errors);
}
