package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

/**
 * User: marc Date: Jul 6, 2008 Time: 9:51:49 AM
 */
public class PercentPropertyEditor extends PropertyEditorSupport {
    private int scale;
    private boolean allowEmpty;
    private NumberFormat numberFormat = NumberFormat.getPercentInstance();

    /**
     * No argument constructor using sensible defaults
     */
    public PercentPropertyEditor() {
        this(0, true);
    }

    public PercentPropertyEditor(int scale, boolean allowEmpty) throws IllegalArgumentException {
        this.scale = scale;
        this.allowEmpty = allowEmpty;
        this.numberFormat.setMinimumFractionDigits(scale);
        this.numberFormat.setMaximumFractionDigits(scale);
    }

    public String getAsText() {
        BigDecimal value = (BigDecimal) getValue();
        if (value == null) {
            return StringUtils.EMPTY;
        } else {
            return this.numberFormat.format(value.doubleValue());
        }
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isNotEmpty(text)) {
            try {
                String _text = text;
                _text = StringUtils.remove(_text, '%');
                BigDecimal bigNumber = new BigDecimal(_text);
                this.setValue(bigNumber.divide(hundred, this.scale + 2, RoundingMode.HALF_UP));
            } catch (IllegalArgumentException iae) {
                // Re-throw but with a friendlier error message
                String ex = "Please enter a number in the format '" + this.numberFormat.format(0.999999) + "'";
                throw new IllegalArgumentException(ex, iae.getCause());
            }
        } else if (!this.allowEmpty) {
            throw new IllegalArgumentException("Text cannot be empty");
        } else {
            this.setValue(null);
        }
    }

    private static final BigDecimal hundred = BigDecimal.valueOf(100d);
}
