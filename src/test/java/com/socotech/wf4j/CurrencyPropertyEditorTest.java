package com.socotech.wf4j;

import java.math.RoundingMode;
import java.util.Currency;

import junit.framework.TestCase;
import org.junit.Test;

public class CurrencyPropertyEditorTest extends TestCase {
    @Test
    public void testDefaultConstructor() {
        CurrencyPropertyEditor editor = new CurrencyPropertyEditor();
        // test whole amt with symbol
        editor.setAsText("$100");
        assertEquals("Unable to parse text", "$100.00", editor.getAsText());
        Number value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 100d, value.doubleValue(), 0d);
        // test fractional amt with symbol
        editor.setAsText("$100.12");
        assertEquals("Unable to parse text", "$100.12", editor.getAsText());
        value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 100.12d, value.doubleValue(), 0d);
        // test whole amt without symbol
        editor.setAsText("$100");
        assertEquals("Unable to parse text", "$100.00", editor.getAsText());
        value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 100d, value.doubleValue(), 0d);
        // test commas
        editor.setAsText("1,000");
        assertEquals("Unable to parse text", "$1,000.00", editor.getAsText());
        value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 1000d, value.doubleValue(), 0d);
        // test commas++
        editor.setAsText("1,000,000");
        assertEquals("Unable to parse text", "$1,000,000.00", editor.getAsText());
        value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 1000000d, value.doubleValue(), 0d);
    }

    @Test
    public void testUSDollar() {
        CurrencyPropertyEditor editor = new CurrencyPropertyEditor(Currency.getInstance("USD"),
                0,
                RoundingMode.HALF_UP,
                false);
        editor.setAsText("$1,000");
        assertEquals("Unable to parse text", "$1,000", editor.getAsText());
        Number value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 1000d, value.doubleValue(), 0d);
    }

    @Test
    public void testRoundDownToNearstDollar() {
        CurrencyPropertyEditor editor =
                new CurrencyPropertyEditor(RoundingMode.HALF_DOWN, 0, false);
        editor.setAsText("$500.50");
        assertEquals("Unable to parse text", "$500", editor.getAsText());
        Number value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 500.0d, value.doubleValue(), 0d);
    }

    @Test
    public void testRoundUpToNearstPenny() {
        CurrencyPropertyEditor editor = new CurrencyPropertyEditor(RoundingMode.HALF_UP, false);
        editor.setAsText("$1.256");
        assertEquals("Unable to parse text", "$1.26", editor.getAsText());
        Number value = (Number) editor.getValue();
        assertEquals("Unable to parse value", 1.26d, value.doubleValue(), 0d);
    }
}
