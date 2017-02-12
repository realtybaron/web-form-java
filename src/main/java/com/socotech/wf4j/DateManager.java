/*
 * DateManager.java
 */
package com.socotech.wf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import org.apache.commons.lang.Validate;

/**
 * <p/> A collection of date-related utilities. </p> <p/> <p/> Clients should <i>always </i> use these routines to make sure that all the dates are internally consistent -- that
 * is, the correct formats, the same time zone, and so on. All dates in the system are in the time zone specified in the Configuration object, also in the util package. </p> <p/>
 * <p/> The standard date formats supported by this object are: </p> <ul> <li><b>Standard </b>, 2005-05-24 22:45:12</li> <li><b>Short </b>, 2005-05-24</li> <li><b>Full </b>, Wed
 * 2005-05-24 22:45:12 UTC</li> <li><b>Human </b>, 05/24/2005</li> <li><b>Friendly</b>, Wed, May 24, 2005 4:43:22pm CST</li> <li><b>RFC 822</b>, Sun, 19 May 2002 15:21:36 GMT</li>
 * <li><b>ShortFriendly</b>, May 24, 2005</li> </ul>
 */
public class DateManager {

    /**
     * <p/> Constructor is package-private to prevent accidental instantiation. (The Template object needs access to it, because Velocity doesn't support pure static methods
     * without an instance.) </p>
     */
    DateManager() {
        // noop
    }

    /**
     * <p/> Returns the current time in the time zone specified in the configuration file. </p>
     *
     * @return Now A fully populated date object
     */
    public static Date now() {
        // Create a new Calendar so it's initialized with the now value
        Calendar newCal = Calendar.getInstance(tz);
        return newCal.getTime();
    }

    /**
     * Return the current time minus one month in the time zone specified in the configuration file.
     *
     * @return last week at this time
     */
    public static Date yestermonth() {
        Calendar cal = Calendar.getInstance(tz);
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * Given a Date object, sets its time fields to correspond to 00:00:00 while leaving its date values unaltered.
     *
     * @param d a prepared Date object
     * @return a Date object with its time set to 00:00:00
     */
    public static Date setToStartOfDay(Date d) {
        Validate.notNull(d);
        return DateManager.makeDate(DateManager.getField(d, Calendar.YEAR), DateManager.getField(d, Calendar.MONTH), DateManager.getField(d, Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    /**
     * Given a Date object, sets its time fields to correspond to 23:59:59 while leaving its date values unaltered.
     *
     * @param d a prepared Date object
     * @return a Date object with its time set to 23:59:59
     */
    public static Date setToEndOfDay(Date d) {
        Validate.notNull(d);
        return DateManager.makeDate(DateManager.getField(d, Calendar.YEAR), DateManager.getField(d, Calendar.MONTH), DateManager.getField(d, Calendar.DAY_OF_MONTH), 23, 59, 59);
    }

    /**
     * <p/> Returns the current time in the time zone specified in the configuration file, only with zero for hours, minutes, seconds, and millis. </p>
     *
     * @return A date object for now, only with zero in H M S and millis.
     */
    public static Date today() {
        Date now = DateManager.now();
        return DateManager.makeDate(DateManager.getField(now, Calendar.YEAR), DateManager.getField(now, Calendar.MONTH), DateManager.getField(now, Calendar.DAY_OF_MONTH));
    }

    /**
     * Returns tomorrow, in the time zone specified in the configuration file, only with zero for hours, minutes, seconds, and millis.
     *
     * @return A date object for tomorrow
     */
    public static Date tomorrow() {
        return DateManager.rollDate(DateManager.today(), 1);
    }

    /**
     * <p/> Using today's date/time and a number of days to roll it, this method adds that many dates. Pass in a positive number and it moves forward that many days; negative and
     * it moves back that many days. If you pass in zero, you get the same date object back. </p>
     *
     * @param days The number of days to roll, can be negative
     * @return A new date object
     */
    public static Date rollDate(int days) {
        return rollDate(now(), days);
    }

    /**
     * <p/> Given a date and a number of days to roll it, this method adds that many dates. Pass in a positive number and it moves forward that many days; negative and it moves
     * back that many days. If you pass in zero, you get the same date object back. </p>
     *
     * @param startingDate The starting date
     * @param days         The number of days to roll, can be negative
     * @return A new date object
     */
    public static Date rollDate(Date startingDate, int days) {
        Date rv = startingDate;
        if (days != 0) {
            synchronized (cal) {
                cal.setTime(startingDate);
                cal.add(Calendar.DAY_OF_MONTH, days);
                rv = cal.getTime();
            }
        }
        return rv;
    }

    /**
     * Given a date and a number of minutes to alter it, this method will add the minutes to the original date and return an adjusted date. If you pass in negative minutes, they
     * will be subtracted from the original. <p/> Because of the way the calendar works, it will do the intelligent thing if you specify an overly large number of minutes. e.g.
     * addTime(original, 1440) will add a full day to the original date.
     *
     * @param original the date to modify
     * @param minutes  the number of minutes to alter the original by
     * @return the modified Date
     * @deprecated Use {@link #addTime(Date, int, int)} instead
     */
    @Deprecated
    public static Date addTime(Date original, int minutes) {
        return addTime(original, Calendar.MINUTE, minutes);
    }

    /**
     * Given a date and a number of minutes to alter it, this method will add the minutes to the original date and return an adjusted date. If you pass in negative minutes, they
     * will be subtracted from the original. <p/> Because of the way the calendar works, it will do the intelligent thing if you specify an overly large number of minutes. e.g.
     * addTime(original, 1440) will add a full day to the original date.
     *
     * @param original the date to modify
     * @param field    the field to modify, e.g. Calendar.MINUTE. See the Calendar javadoc "Field Summary" for choices.
     * @param amount   the number of minutes to alter the original by
     * @return the modified Date
     * @throws IllegalArgumentException if the original date is null
     */
    public static Date addTime(Date original, int field, int amount) {
        Validate.notNull(original);

        // Create a new Calendar so it's initialized with the now value
        Calendar newCal = Calendar.getInstance(tz);
        newCal.setTime(original);
        newCal.add(field, amount);
        return newCal.getTime();
    }

    /**
     * <p/> Given year, month, and day, creates a new date with the other values (hour, minutes, and seconds) all set to zero, in the timezone specified in the configuration
     * object. </p>
     *
     * @param year  The year (full four digits)
     * @param month The month (use Calendar.JANUARY and the like)
     * @param day   The day of the month
     * @return A new properly formatted Date instance
     */
    public static Date makeDate(int year, int month, int day) {
        return DateManager.makeDate(year, month, day, 0, 0, 0);
    }

    /**
     * <p/> Given a full timespec (day and exact time), creates and returns a new Date object in the timezone specified in the configuration object. Sets the milliseconds to null.
     * </p>
     *
     * @param year    The year (full four digits)
     * @param month   The month (use Calendar.JANUARY and the like)
     * @param day     The day of the month (between 1 and 31)
     * @param hours   The number of hours (in a 24-hour clock, between 0 and 23
     * @param minutes The number of minutes (between 0 and 59)
     * @param seconds The number of seconds (between 0 and 59)
     * @return A date created from those arguments
     */
    public static Date makeDate(int year, int month, int day, int hours, int minutes, int seconds) {
        synchronized (cal) {
            cal.set(year, month, day, hours, minutes, seconds);
            cal.set(Calendar.MILLISECOND, 0);
            return new Date(cal.getTimeInMillis());
        }
    }

    /**
     * <p/> Given a Date object, returns a string in the canonical format. The format is yyyy-MM-dd kk:mm:ss. The time zone is the time zone specified in the configuration object.
     * </p>
     *
     * @param date The date to format
     * @return The string form of the date
     */
    public static String dateToString(Date date) {
        synchronized (stdDateFormat) {
            return stdDateFormat.format(date);
        }
    }

    /**
     * <p/> Given a Date object, returns a string in the short human-friendly format. The format is MM/dd/yyyy. The time zone is the time zone specified in the configuration
     * object. </p>
     *
     * @param date The date to format
     * @return The human-friendly short form of the date
     */
    public static String dateToHumanString(Date date) {
        synchronized (humanDateFormat) {
            return humanDateFormat.format(date);
        }
    }

    /**
     * <p/> Given a string of the short-human format, returns a date object in the standard time zone parsed from that string. </p>
     *
     * @param date The short string form of a date
     * @return A date object from that string
     * @throws ParseException if it can't be parsed
     */
    public static Date humanStringToDate(String date) throws ParseException {
        synchronized (humanDateFormat) {
            return humanDateFormat.parse(date);
        }
    }

    /**
     * <p/> Given a string of the short-human format, returns a date object in the standard time zone parsed from that string. Because this is specifically an end date, its time is
     * set to the last second before midnight, thereby capturing as much of the day as possible. </p>
     *
     * @param date The short string form of a date
     * @return A date object from that string
     * @throws ParseException if it can't be parsed
     */
    public static Date humanStringToEndDate(String date) throws ParseException {
        synchronized (humanDateFormat) {
            Date tempDate = humanDateFormat.parse(date);
            DateManager.setHMS(tempDate, 23, 59, 59);
            return tempDate;
        }
    }

    /**
     * <p/> Given a date and a field, fetches the value of that field given the standard time zone. Any of the legal date fields, like <code>Calendar.DAY_OF_MONTH</code>, are legal
     * in this function. </p>
     *
     * @param date  The date to check
     * @param field The field to return
     * @return The value of that field
     */

    public static int getField(Date date, int field) {
        synchronized (cal) {
            cal.setTime(date);
            return cal.get(field);
        }
    }

    /**
     * <p/> A convenience function to change the hours minutes and seconds in one fell swoop. </p>
     *
     * @param date    The date object to change
     * @param hours   New hours value
     * @param minutes New minutes value
     * @param seconds New seconds value
     */
    public static synchronized void setHMS(Date date, int hours, int minutes, int seconds) {
        synchronized (cal) {
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);
            cal.set(Calendar.MILLISECOND, 0);
            date.setTime(cal.getTimeInMillis());
        }
    }

    /**
     * <p/> The standard time zone. </p>
     */
    private final static TimeZone tz;

    /**
     * <p/> A static calendar for all these routines. </p>
     */
    private final static Calendar cal;

    /**
     * <p/> A static date formatter for the system-wide standard format. </p>
     */
    private final static SimpleDateFormat stdDateFormat;

    /**
     * <p/> A static date formatter for the system-wide short format. </p>
     */
    private final static SimpleDateFormat shortDateFormat;

    /**
     * <p/> A static date formatter for the system-wide medium format. </p>
     */
    private final static SimpleDateFormat mediumDateFormat;

    /**
     * <p/> A static date formatter for a short human-friendly format. </p>
     */
    private final static SimpleDateFormat humanDateFormat;

    /**
     * <p/> A static date formatter for a full human-readable format. </p>
     */
    private final static SimpleDateFormat fullDateFormat;

    /**
     * <p/> A static date formatter for a "friendly" date format, for non-techies. </p>
     */
    private final static SimpleDateFormat friendlyDateFormat;

    /**
     * A static date formatter for a short friendly date format.
     */
    private final static SimpleDateFormat shortFriendlyDateFormat;

    /**
     * A static date formatter for the dates that RSS 2.0 comprehends.
     */
    private final static SimpleDateFormat rfc822DateFormat;

    /**
     * A static month formatter for a full human-readable format
     */
    private final static SimpleDateFormat fullMonthFormat;

    /**
     * For "May 2002" style formats
     */
    private final static SimpleDateFormat fullMonthYearFormat;

    /**
     * For "Wed, Sep 9 2012 04:32:12" type of format, preferred by Andrew
     */
    private final static SimpleDateFormat shortDiscoveryFormat;

    /**
     * A collection of date formatters for the unknown date formatter.
     */
    private final static LinkedList<DateFormat> commonNumberDateFormatters;

    private final static LinkedList<DateFormat> commonStringDateFormatters;

    /**
     * <p/> The earliest legal date for arbitrary parsing. </p>
     */
    private final static Date earlyDate;

    // Class initialization

    static {

        // The standard date formatter is always the standard zone
        tz = TimeZone.getDefault();
        cal = Calendar.getInstance(tz);
        stdDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        stdDateFormat.setTimeZone(tz);
        shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        shortDateFormat.setTimeZone(tz);
        mediumDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        mediumDateFormat.setTimeZone(tz);
        humanDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        humanDateFormat.setTimeZone(tz);
        // If setLenient is not set to false, the format is validated in a lazy way.
        // In other words, something like 35/21/2006 would pass, or even 12/12/200T
        humanDateFormat.setLenient(false);
        friendlyDateFormat = new SimpleDateFormat("EEE, MMMM d, yyyy hh:mm:ss a zzz");
        friendlyDateFormat.setTimeZone(tz);
        rfc822DateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
        rfc822DateFormat.setTimeZone(tz);
        shortFriendlyDateFormat = new SimpleDateFormat("MMMM d, yyyy");
        shortFriendlyDateFormat.setTimeZone(tz);
        shortDiscoveryFormat = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
        shortDiscoveryFormat.setTimeZone(tz);

        // The full date format allows users to view in different zones
        fullDateFormat = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss zzz");
        fullMonthFormat = new SimpleDateFormat("MMMM");
        fullMonthYearFormat = new SimpleDateFormat("MMMM yyyy");

        // These are for the unknownStringToDate method
        commonNumberDateFormatters = new LinkedList<DateFormat>();
        commonNumberDateFormatters.add(shortDateFormat);
        commonNumberDateFormatters.add(stdDateFormat);
        commonNumberDateFormatters.add(fullDateFormat);
        commonNumberDateFormatters.add(humanDateFormat);
        commonNumberDateFormatters.add(shortDiscoveryFormat);
        DateFormat df = DateFormat.getDateInstance();
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("MM-dd-y");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("MM/dd/y");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("dd MMM yyyy");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("yyyy/MM/dd");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("MM.dd.yyyy");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);
        df = new SimpleDateFormat("MMddyy");
        df.setTimeZone(tz);
        commonNumberDateFormatters.add(df);

        // These are also for the unknownStringToDate method
        commonStringDateFormatters = new LinkedList<DateFormat>();
        df = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        df.setTimeZone(tz);
        // This one (sFDF) needs to come before the 'MMM yyyy' one, because
        // that one is more liberal and erroneously catches sFDF strings too.
        commonStringDateFormatters.add(shortFriendlyDateFormat);
        commonStringDateFormatters.add(df);
        df = new SimpleDateFormat("MMM yyyy");
        df.setTimeZone(tz);
        commonStringDateFormatters.add(df);
        df = new SimpleDateFormat("MMM, yyyy");
        df.setTimeZone(tz);
        commonStringDateFormatters.add(df);
        df = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
        df.setTimeZone(tz);
        commonStringDateFormatters.add(df);
        df = new SimpleDateFormat("MMM. dd, yyyy");
        df.setTimeZone(tz);
        commonStringDateFormatters.add(df);
        df = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
        df.setTimeZone(tz);
        commonStringDateFormatters.add(df);
        commonStringDateFormatters.add(friendlyDateFormat);


        // This is the earliest date that can be parsed from an unknown format
        // (for the leading number formatters)
        cal.add(Calendar.YEAR, -30);
        earlyDate = new Date(cal.getTimeInMillis());

        // Java thinks the default time zone is the same as the one
        // the machine is running in... for this object, it's the
        // configuration time zone.
        TimeZone.setDefault(tz);
    }
}
