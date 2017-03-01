package com.socotech.wf4j;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Converts between a currency value's numeric and textual representation
 * <p/>
 * If using this editor with FormBinder, you'll need to sub-class and provide a no-argument constructor since this class doesn't have one.
 *
 * @see FormBinder
 */
public class CurrencyPropertyEditor extends PropertyEditorSupport {
    public final int scale;
    public final boolean allowEmpty;
    public final Currency currency;
    public final RoundingMode rounding;
    public final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    public CurrencyPropertyEditor() {
        this(Currency.getInstance("USD"), RoundingMode.HALF_EVEN, false);
    }

    public CurrencyPropertyEditor(Currency currency) {
        this(currency, RoundingMode.HALF_EVEN, false);
    }

    public CurrencyPropertyEditor(boolean allowEmpty) {
        this(Currency.getInstance("USD"), RoundingMode.HALF_EVEN, allowEmpty);
    }

    public CurrencyPropertyEditor(Currency currency, int scale) {
        this(currency, scale, RoundingMode.HALF_EVEN, false);
    }

    public CurrencyPropertyEditor(RoundingMode rounding, boolean allowEmpty) {
        this(Currency.getInstance("USD"), rounding, allowEmpty);
    }

    public CurrencyPropertyEditor(RoundingMode rounding, int scale, boolean allowEmpty) {
        this(Currency.getInstance("USD"), scale, rounding, allowEmpty);
    }

    public CurrencyPropertyEditor(Currency currency, RoundingMode rounding, boolean allowEmpty) {
        this(currency, currency.getDefaultFractionDigits(), rounding, allowEmpty);
    }

    public CurrencyPropertyEditor(Currency currency, int scale, RoundingMode rounding, boolean allowEmpty) {
        this.scale = scale;
        this.currency = currency;
        this.rounding = rounding;
        this.allowEmpty = allowEmpty;
        this.numberFormat.setCurrency(currency);
        this.numberFormat.setMinimumFractionDigits(scale);
        this.numberFormat.setMaximumFractionDigits(scale);
    }

    public String getAsText() {
        Number value = (Number) getValue();
        if (value == null) {
            return "";
        } else {
            return this.numberFormat.format(value.doubleValue());
        }
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            try {
                String _text = text;
                _text = StringUtils.remove(_text, this.currency.getSymbol());
                _text = StringUtils.remove(_text, ',');
                Number number = NumberUtils.createNumber(_text);
                double value = number.doubleValue();
                BigDecimal biggie = new BigDecimal(value).setScale(this.scale, this.rounding);
                setValue(biggie);
            } catch (IllegalArgumentException iae) {
                // Rethrow but with a friendlier error message
                String ex = "Please enter a number in the format '" + this.numberFormat.format(999.99) + "'";
                throw new IllegalArgumentException(ex);
            }
        }
    }
}
