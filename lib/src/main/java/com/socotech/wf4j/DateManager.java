/*
 * DateManager.java
 */
package com.socotech.wf4j;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;

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
        //nop
    }

    /**
     * Returns the earliest time relevant to the system, i.e. August 1st, 2005
     *
     * @return the system's earliest recorded time
     */
    public static Date epoch() {
        Calendar newCal = Calendar.getInstance(tz);
        newCal.set(2005, 7, 1, 0, 0, 0);
        return newCal.getTime();
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
     * Get a date +/- days from now
     *
     * @param offset +/- days
     * @return offset date
     */
    public static Date days(int offset) {
        return addTime(Calendar.DATE, offset);
    }

    /**
     * Return the current time minus one day in the time zone specified in the configuration file.
     *
     * @return yesterday at this time
     */
    public static Date yesterday() {
        Calendar cal = Calendar.getInstance(tz);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * Return the current time minus one week in the time zone specified in the configuration file.
     *
     * @return last week at this time
     */
    public static Date yesterweek() {
        return yesterweek(now());
    }

    /**
     * Return the current time minus one week in the time zone specified in the configuration file.
     *
     * @param d reference date
     * @return last week at this time
     */
    public static Date yesterweek(Date d) {
        Calendar cal = Calendar.getInstance(tz);
        cal.setTime(d);
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        return cal.getTime();
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
     * Return the current time minus plus months months in the time zone specified in the configuration file.
     *
     * @param months offset in months
     * @return months months from now, at this time.
     */
    public static Date monthsFromNow(int months) {
        Calendar cal = Calendar.getInstance(tz);
        cal.add(Calendar.MONTH, months);
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
     * <p/> Returns the calendar used in all of the date manager routines. </p>
     *
     * @return The calendar used in all of the routines
     */
    public static Calendar getCalendar() {
        return cal;
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
     * <p/> Given a date and a number of hours to roll it, this method adds that many hours. Pass in a positive number and it moves forward that many hours; negative and it moves
     * back that many hours. If you pass in zero, you get the same date object back. </p>
     *
     * @param startingDate The starting date
     * @param hours        The number of hours to roll, can be negative
     * @return A new date object
     */
    public static Date rollDateHours(Date startingDate, int hours) {
        Date rv = startingDate;
        if (hours != 0) {
            synchronized (cal) {
                cal.setTime(startingDate);
                cal.add(Calendar.HOUR, hours);
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
     * Using the current time and a number of minutes to alter it, this method will add the minutes to the original date and return an adjusted date. If you pass in negative
     * minutes, they will be subtracted from the original. <p/> Because of the way the calendar works, it will do the intelligent thing if you specify an overly large number of
     * minutes. e.g. addTime(original, 1440) will add a full day to the original date.
     *
     * @param field  the field to modify, e.g. Calendar.MINUTE. See the Calendar javadoc "Field Summary" for choices.
     * @param amount the number of minutes to alter the original by
     * @return the modified Date
     * @throws IllegalArgumentException if the original date is null
     */
    public static Date addTime(int field, int amount) {
        return addTime(DateManager.now(), field, amount);
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
     * Given a date, calculate the difference between a the current time and the specific field on the date.
     *
     * @param d     date
     * @param field calendar field
     * @return difference
     */
    public static int diffTime(Date d, int field) {
        return diffTime(DateManager.now(), d, field);
    }

    /**
     * Given two dates, calculate the difference between a specific field on each date.
     *
     * @param d1    date one
     * @param d2    date two
     * @param field calendar field
     * @return difference
     */
    public static int diffTime(Date d1, Date d2, int field) {
        Validate.notNull(d1);
        Validate.notNull(d2);
        // calc the absolute difference
        long diff = Math.abs(d2.getTime() - d1.getTime());
        // switch on the calendar field
        switch (field) {
            case Calendar.DATE:
                return Math.round((float) diff / DateUtils.MILLIS_PER_DAY);
            case Calendar.HOUR:
                return Math.round((float) diff / DateUtils.MILLIS_PER_HOUR);
            case Calendar.MINUTE:
                return Math.round((float) diff / DateUtils.MILLIS_PER_MINUTE);
            case Calendar.SECOND:
                return Math.round((float) diff / DateUtils.MILLIS_PER_SECOND);
            default:
                throw new IllegalArgumentException("Unsupported calendar field: " + field);
        }
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
     * A straightfoward routine that tells you if "former" is strictly prior to "latter".
     *
     * @param former the date that supposedly comes first
     * @param latter the date that supposedly comes second
     * @return true if former does, in fact, precede latter; false otherwise
     * @throws IllegalArgumentException if either arg is null
     */
    public static boolean isBefore(Date former, Date latter) {
        Validate.notNull(former);
        Validate.notNull(latter);

        Date f = new Date(former.getTime());
        Date l = new Date(latter.getTime());

        if (f.compareTo(l) < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A straightfoward routine that tells you if "latter" is strictly after "former".
     *
     * @param latter the date that supposedly comes second
     * @param former the date that supposedly comes first
     * @return true if former does, in fact, precede latter; false otherwise
     * @throws IllegalArgumentException if either arg is null
     */
    public static boolean isAfter(Date latter, Date former) {
        Validate.notNull(former);
        Validate.notNull(latter);

        if (latter.compareTo(former) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A straightfoward routine that tells you if "former" is prior to (or equal to) "latter".
     *
     * @param former the date that supposedly comes first
     * @param latter the date that supposedly comes second
     * @return true if former does, in fact, precede latter; false otherwise
     * @throws IllegalArgumentException if either arg is null
     */
    public static boolean isBeforeOrEqualTo(Date former, Date latter) {
        Validate.notNull(former);
        Validate.notNull(latter);

        if (former.compareTo(latter) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A straightfoward routine that tells you if "latter" is after (or equal to) "former".
     *
     * @param latter the date that supposedly comes second
     * @param former the date that supposedly comes first
     * @return true if former does, in fact, precede latter; false otherwise
     * @throws IllegalArgumentException if either arg is null
     */
    public static boolean isAfterOrEqualTo(Date latter, Date former) {
        Validate.notNull(former);
        Validate.notNull(latter);

        if (latter.compareTo(former) >= 0) {
            return true;
        } else {
            return false;
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
     * <p/> Given a Date object, returns a string in the canonical format. The format is yyyy-MM-dd kk:mm:ss. The time zone is the time zone specified in the configuration object.
     * </p>
     *
     * @param date       The date to format
     * @param defaultVal If date is null or cannot be parsed, return this value
     * @return The string form of the date
     */
    public static String dateToString(Date date, String defaultVal) {
        if (date == null) {
            return defaultVal;
        }
        synchronized (stdDateFormat) {
            try {
                return stdDateFormat.format(date);
            } catch (Exception e) {
                return defaultVal;
            }
        }
    }

    /**
     * Given a date object, return a string in the form that RSS 2.0 undertands, e.g. Sun, 19 May 2002 15:21:36 GMT
     *
     * @param date the date to format
     * @return the string form of the date
     */
    public static String dateToRfc822String(Date date) {
        synchronized (stdDateFormat) {
            return rfc822DateFormat.format(date);
        }
    }

    /**
     * <p/> Given a Date object, returns a string in the short canonical format. The format is yyyy-MM-dd. The time zone is the time zone specified in the configuration object.
     * </p>
     *
     * @param date The date to format
     * @return The string form of the date
     */
    public static String dateToShortString(Date date) {
        synchronized (shortDateFormat) {
            return shortDateFormat.format(date);
        }
    }

    /**
     * Given a date object, returns it in the medium canonical format, MM/dd/yyyy HH:mm.
     *
     * @param date da date
     * @return the medium string form of the date
     */
    public static String dateToMediumString(Date date) {
        synchronized (mediumDateFormat) {
            return mediumDateFormat.format(date);
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
     * <p/> Given a Date object, returns a string in the long friendly format, which non-techies will like because it is needlessly long. The format is like "Sunday, May 3, 2005
     * 04:32:22pm PST". The time zone is the time zone specified in the configuration object. </p>
     *
     * @param date The date to format
     * @return The human-friendly long form of the date
     */
    public static String dateToFriendlyString(Date date) {
        synchronized (friendlyDateFormat) {
            return friendlyDateFormat.format(date);
        }
    }

    /**
     * <p/> Given a Date object, returns a string in the short friendly format, which non-techies will like because it does not frighten them with such things as "time zone" or
     * "minutes". The format is like "May 3, 2005". The time zone is the time zone specified in the configuration object. </p>
     *
     * @param date The date to format
     * @return The human-friendly short form of the date
     */
    public static String dateToShortFriendlyString(Date date) {
        synchronized (shortFriendlyDateFormat) {
            return shortFriendlyDateFormat.format(date);
        }
    }

    /**
     * Returns a date in short discovery string format
     *
     * @param date the date to format
     * @return the discovery short form
     */
    public static String dateToShortDiscoveryString(Date date) {
        synchronized (shortDiscoveryFormat) {
            return shortDiscoveryFormat.format(date);
        }
    }

    /**
     * <p/> Given a Date object and a time zone, returns a string in the the full format (useful for official but human-readable formats). The format is like "Wed 2004-01-05 22:34
     * EST". </p>
     *
     * @param date The date to format
     * @param zone The time zone to convert it to
     * @return The string form of the date
     */
    public static String dateToFullString(Date date, String zone) {
        synchronized (fullDateFormat) {
            TimeZone oldTz = fullDateFormat.getTimeZone();
            TimeZone newTz = TimeZone.getTimeZone(zone);
            fullDateFormat.setTimeZone(newTz);
            String val = fullDateFormat.format(date);
            fullDateFormat.setTimeZone(oldTz);
            return val;
        }
    }

    /**
     * <p/> Given a Date object, returns the month in the the full format (useful for official but human-readable formats). The format is like "August" or "November". </p>
     *
     * @param date The date to format
     * @return The string form of the date
     */
    public static String dateToFullMonth(Date date) {
        synchronized (fullMonthFormat) {
            return fullMonthFormat.format(date);
        }
    }

    /**
     * Given a date object, returns the date in the form like "August 2009"
     *
     * @param date the date
     * @return string form of the date
     */
    public static String dateToFullMonthYear(Date date) {
        synchronized (fullMonthYearFormat) {
            return fullMonthYearFormat.format(date);
        }
    }

    /**
     * <p/> Given a string of the standard format, returns a date object in the standard time zone parsed from that string. </p>
     *
     * @param date The string form of a date
     * @return A date object from that string
     * @throws ParseException if it can't be parsed
     */
    public static Date stringToDate(String date) throws ParseException {
        synchronized (stdDateFormat) {
            return stdDateFormat.parse(date);
        }
    }

    /**
     * <p/> Given a string of the short format, returns a date object in the standard time zone parsed from that string. </p>
     *
     * @param date The short string form of a date
     * @return A date object from that string
     * @throws ParseException if it can't be parsed
     */
    public static Date shortStringToDate(String date) throws ParseException {
        synchronized (shortDateFormat) {
            return shortDateFormat.parse(date);
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
     * <p/> Given a string of the full format, returns a date object in the standard time zone parsed from that string. </p>
     *
     * @param date The full string form of a date
     * @return A date object from that string
     * @throws ParseException if it can't be parsed
     */
    public static Date fullStringToDate(String date) throws ParseException {
        synchronized (fullDateFormat) {
            return fullDateFormat.parse(date);
        }
    }

    /**
     * <p/> Given a date string of an unknown format, tries to parse it into a Date object. Obviously the success or failure of this method depends on what format of string we
     * get.
     * <p/>
     *
     * @param date A string form of a date of an unknown format
     * @return A date object from that string
     * @throws ParseException if the string can't be parsed
     */
    public static Date unknownStringToDate(String date) throws ParseException {
        // We may get dates of the form ##/##/#### or similar, with no way to know if the
        // month is first or if the day is first. Most of the time this function is called
        // on content publication dates, so hint to the function that the most likely
        // dates are those that are sooner rather than later.
        int[] days = new int[]{1, 30, 365, 3650};
        for (int d : days) {
            Date inFuture;
            synchronized (cal) {
                cal.setTime(now());
                cal.add(Calendar.DATE, d);
                inFuture = cal.getTime();
            }
            Date guess = unknownStringToDate(date, inFuture);
            if (guess != null) {
                return guess;
            }
        }

        // Got this far? I guess we got nothin'.
        throw new ParseException("Couldn't parse " + date + " into any known date format", 0);
    }

    /**
     * <p/> Given a date string of an unknown format, tries to parse it into a Date object. Obviously the success or failure of this method depends on what format of string we get.
     * </p> <p/> Right now the following formats are supported: </p> <ul> <li>January 5, 2003</li> <li>Jan 5, 2003</li> <li>01-05-2003</li> <li>01-05-03</li> <li>01/05/2003</li>
     * <li>01/05/03</li> <li>Wednesday, January 5, 2003</li> <li>The other date formats otherwise accepted by this <code>DateManager</code>.</li> <ul> <p/> Because of the wide
     * variety of possible date formats, we only return dates between ten years ago and a future date (from the time this method is run). That's a fairly arbitrary choice, but as
     * this method is used for publication dates of content, it seems a reasonable choice. </p>
     *
     * @param date    A string form of a date of an unknown format
     * @param ceiling A date which is considered obsurdly futuristic
     * @return A date object from that string
     * @throws ParseException if the string can't be parsed
     */
    public static Date unknownStringToDate(String date, Date ceiling) {
        // September is the only month likely to be abbreviated with four
        // letters ("Sept.") so fix that...
        date = date.replaceFirst("Sept\\.", "Sep\\.");
        date = date.replaceFirst("Sept ", "Sep ");
        date = StringUtils.strip(date);

        // First try the leading-number formatters, limiting the earliest
        // parseable date to earlyDate.
        for (DateFormat df : commonNumberDateFormatters) {
            try {
                Date rv = null;

                synchronized (df) {
                    rv = df.parse(date);
                }

                if (rv.before(earlyDate)) {
                    continue;
                }
                if (rv.after(ceiling)) {
                    continue;
                }

                // Got this far? Then we parsed OK; return that date
                return rv;

            } catch (ParseException pe) {
                // It's OK that this didn't parse -- just skip onto the next one
            }
        }

        // Now try the leading-string formatters, with no earlyDate
        // restriction.
        for (DateFormat df : commonStringDateFormatters) {
            try {
                Date rv = null;

                synchronized (df) {
                    rv = df.parse(date);
                }

                if (rv.after(ceiling)) {
                    continue;
                }

                // Got this far? Then we parsed OK; return that date
                return rv;

            } catch (ParseException pe) {
                // It's OK that this didn't parse -- just skip onto the next one
            }
        }

        // Got this far? We failed to parse.
        return null;
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
     * <p/> Given a date, returns a new date object that is the first of that month at 0:00:00. For example, if you pass in the date for February 3, 2006, 3:33PM, you'll get back a
     * date February 1, 2006, 0:00:00. </p>
     *
     * @param d the date you're interested in
     * @return A new date that's on the first of that month
     */
    public static Date getFirstOfMonth(Date d) {
        Date rv = null;
        synchronized (cal) {
            cal.setTime(d);
            rv = DateManager.makeDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        }
        return rv;
    }

    /**
     * <p/> Given a date, returns a new date object that is the end of that month at 23:59:59. For example, if you pass in the date for February 3, 2006, 3:33PM, you'll get back a
     * date February 28, 2006, 23:59:59. </p>
     *
     * @param d the date you're interested in
     * @return A new date that's on the last of that month
     */
    public static Date getLastOfMonth(Date d) {
        Date rv = null;
        synchronized (cal) {
            cal.setTime(d);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            rv = DateManager.makeDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), lastDay, 23, 59, 59);
        }
        return rv;
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
     * Decodes a time value in "hh:mm:ss" format and returns it as milliseconds since midnight.  Useful for treating as a duration to add to another known date, as
     * "otherDate.getTime() + decodeTime(duration)".
     *
     * @param timeExpr a time expressed in hh:mm:ss form
     * @return the equivalent number of milliseconds, suitable for adding
     * @throws IllegalArgumentException if the arg is null or not paresable
     */
    public static synchronized int decodeTime(String timeExpr) throws IllegalArgumentException {
        Validate.notEmpty(timeExpr);

        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        f.setTimeZone(utcTimeZone);
        f.setLenient(false);
        ParsePosition p = new ParsePosition(0);
        Date d = f.parse(timeExpr, p);
        if (d == null || !isRestOfStringBlank(timeExpr, p.getIndex())) {
            throw new IllegalArgumentException("Invalid time value (hh:mm:ss): \"" + timeExpr + "\".");
        }
        return (int) d.getTime();
    }

    /**
     * A small utility method used by the decodeTime() method, above.
     *
     * @param s string to test
     * @param p position within the string
     * @return true if the rest of the string is blank
     */
    private static boolean isRestOfStringBlank(String s, int p) {
        while (p < s.length() && Character.isWhitespace(s.charAt(p))) {
            p++;
        }
        return p >= s.length();
    }

    /**
     * <p/> A very simple helper function to return the current year, helpful in things like copyright notices and the like. </p>
     *
     * @return the current four-digit year.
     */
    public static int getCurrentYear() {
        return getField(now(), Calendar.YEAR);
    }

    /**
     * A very simple helper function to return the current day-of-month, e.g. 25.
     *
     * @return the current day-of-month
     */
    public static int getCurrentDayOfMonth() {
        return getField(now(), Calendar.DATE);
    }

    /**
     * Get the number of days in the current month
     *
     * @return number of days
     */
    public static int getDaysInCurrentMonth() {
        return cal.getMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * A helper function to compute whether two dates occurred within the same month and year.  So, Jan/5/06 == Jan/15/06, but Jan/1/06 != Jan/1/07.
     *
     * @param first  the first date to compare
     * @param second the second date to compare
     * @return true if they share the same month and year, false otherwise
     * @throws IllegalArgumentException if either date is null
     */
    public static boolean isShareSameMonth(Date first, Date second) {
        Validate.notNull(first);
        Validate.notNull(second);

        Calendar calFirst = Calendar.getInstance();
        Calendar calSecond = Calendar.getInstance();
        calFirst.setTime(first);
        calSecond.setTime(second);

        return (calFirst.get(Calendar.MONTH) == calSecond.get(Calendar.MONTH)) && (calFirst.get(Calendar.YEAR) == calSecond.get(Calendar.YEAR));
    }

    /**
     * A helper function to compute whether two dates occurred within the same year.  So, Jan/5/06 == Oct/15/06.
     *
     * @param first  the first date to compare
     * @param second the second date to compare
     * @return true if they share the same month and year, false otherwise
     * @throws IllegalArgumentException if either date is null
     */
    public static boolean isShareSameYear(Date first, Date second) {
        Validate.notNull(first);
        Validate.notNull(second);

        Calendar calFirst = Calendar.getInstance();
        Calendar calSecond = Calendar.getInstance();
        calFirst.setTime(first);
        calSecond.setTime(second);

        return calFirst.get(Calendar.YEAR) == calSecond.get(Calendar.YEAR);
    }

    /**
     * Simple helper to return the month and year in a
     *
     * @param month
     * @param year
     * @return
     */
    public String monthAndYearString(int month, int year) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] monthNames = dfs.getMonths();
        return monthNames[month] + " " + year;
    }


    /**
     * <p/> Helper function to validate a start and end date. Throws an invalid argument exception if either start or end date is null, or if the end date isn't strictly after the
     * start date. </p>
     *
     * @param start the start date
     * @param end   the end date
     */
    public static void validateDateRange(Date start, Date end) {
        Validate.notNull(start);
        Validate.notNull(end);
        Validate.isTrue(end.after(start));
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
