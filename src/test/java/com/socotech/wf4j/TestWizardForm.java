package com.socotech.wf4j;

/**
 * User: marc Date: Jan 22, 2010 Time: 5:18:30 PM
 */
public class TestWizardForm extends TestForm implements WizardForm {
    private int page;
    private String choice;

    @Override
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
