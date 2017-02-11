package com.socotech.wf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FormBinder.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FormBinder {
    /**
     * The name of the property to which binding is applied.
     *
     * @return a specific property name or a regular expression used to match property names
     */
    String property() default ".*";

    /**
     * The type of property to which binding is applied.
     *
     * @return a class type
     */
    Class typeClass() default Object.class;

    /**
     * The class type of the editor used to bind
     *
     * @return editor class
     */
    Class editorClass();
}
