package com.socotech.wf4j;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/11/17
 * Time: 3:58 PM
 */
public @interface Action {
    /**
     * Class name
     *
     * @return action class
     */
    Class type();

    /**
     * URL path
     *
     * @return path
     */
    String path();
}
