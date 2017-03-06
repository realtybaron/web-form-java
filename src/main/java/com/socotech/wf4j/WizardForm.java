package com.socotech.wf4j;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: marc
 * Date: 2/25/17
 * Time: 6:03 PM
 */
public interface WizardForm extends Serializable {
    int getPage();

    String getChoice();
}
