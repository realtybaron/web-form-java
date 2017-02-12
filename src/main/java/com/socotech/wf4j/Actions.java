package com.socotech.wf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/11/17
 * Time: 3:57 PM
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Actions {
    /**
     * Actions
     *
     * @return actions
     */
    Action[] actions();
}
