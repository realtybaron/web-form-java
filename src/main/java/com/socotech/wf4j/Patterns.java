/*
 * Patterns
 */
package com.socotech.wf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * <p/> A set of Regular Expression patterns of use throughout Conductor. They're precompiled for
 * speed, and in one place so that we're always consistent. </p> <p/> <p/> To use a pattern
 * directly, use the utility function <code>matches</code>: </p> <p/>
 * <pre>
 * if (Patterns.matches(Patterns.HUMAN_NAMES, p.getFirstName())) {
 * 	addPerson(p);
 * }
 * </pre>
 * <p/> <p/> You can also pass in patterns to the static utility methods in the
 * <code>FieldValidation</code> object, which check lengths. (We could put the lengths in the
 * patterns, but I think that it adds to pattern reuse if we split it out. Debateable.) </p>
 */
public class Patterns {
  /**
   * one or more white spaces
   */
  public static Pattern WHITESPACE = Pattern.compile("\\s+");

  /**
   * Google AdWords slot ID
   */
  public static Pattern ADWORDS_UNIT = Pattern.compile("\\d{10}");

  /**
   * Google AdWords client ID
   */
  public static Pattern ADWORDS_CLIENT = Pattern.compile("(ca-)?pub-\\d{16}");

  /**
   * @link http://www.mkyong.com/regular-expressions/how-to-validate-hex-color-code-with-regular-expression/
   */
  public static Pattern HEX_COLOR = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

  /**
   * <p/> A regular expression for matching people's first and last names. The following characters
   * are in this set. </p> <ul> <li>Uppercase and lowercase letters.</li> <li>Digits (for names like
   * James Edward 3rd).</li> <li>Space (for names like von Holzen).</li> <li>Dash (for last names
   * like Widsloff-Smythe).</li> <li>Single quote (for last names like O'Donnell).</li> <li>The
   * period and comma (for names like Smith, M.D.).</li> </ul>
   */
  public static Pattern HUMAN_NAMES = Pattern.compile("\\p{Alpha}[\\p{Alnum}-\\.', ]*");

  /**
   * <p/> A set of characters for passwords. The following characters are in this set. </p> <ul>
   * <li>Uppercase and lowercase letters.</li> <li>Digits.</li> <li>Punctuation characters.</li>
   * </ul>
   */
  public static Pattern PASSWORDS = Pattern.compile("[\\p{Alnum}\\p{Punct}]*");

  /**
   * <p/> A set of characters for members' screen names. The following characters are in this set.
   * </p> <ul> <li>Uppercase and lowercase letters.</li> <li>Digits.</li> <li>These characters:
   * at-sign, dash, dot, underscore.</li> </ul>
   */
  public static Pattern SCREEN_NAMES = Pattern.compile("[\\p{Alnum}@\\.-_]*");

  /**
   * <p/> A pattern for identifying legal email addresses. This pattern is supposed to accept forms
   * where the user name is composed of lower-case letters, numbers, the underscore, the dot, and the
   * dash; and where the domain is lower-case letters, numbers, underscore, and dash, with at least a
   * TLD as lower-case letters. (We are forcing email addresses to be all lower- case so as to ensure
   * that we don't end up with practical duplicates in the database.) </p>
   */
  public static Pattern EMAIL_ADDRESS =
      Pattern.compile("[_a-z0-9-]+(\\.[_'a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.[a-z]+");

  /**
   * <p/> Given a candidate email address, returns a standardized version of that email address for
   * inclusion in the database. The standardized version is one that is all lower case with no
   * leading or trailing whitespace. </p>
   *
   * @param emailAddress The candidate email address
   * @return The same string only without leading or trailing whitespace and all lower case.
   */
  public static String standardizeEmailAddress(String emailAddress) {
    return StringUtils.lowerCase(StringUtils.strip(emailAddress));
  }

  /**
   * <p/> A pattern for identifying a publication keyword. Most things are not allowed -- keywords
   * are very plain. </p>
   */
  public static Pattern KEYWORD = Pattern.compile("[\\p{Alnum}]+");

  /**
   * <p/>
   * A pattern for helping with structure text. The allowed characters include: <ul> <li>Uppercase
   * and lowercase letters.</li> <li>Digits.</li> <li>White space.</li> <li>Punctuation.</li> </ul>
   */
  public static Pattern STRUCTURED_TEXT = Pattern.compile("[\\p{Alnum}\\p{Punct}\\p{Space}]*");

  /**
   * <p/> Alphabetic characters only. </p>
   */
  public static Pattern ALPHA = Pattern.compile("\\p{Alpha}*");

  /**
   * <p/> Alphanumeric characters only. </p>
   */
  public static Pattern ALPHANUMERIC = Pattern.compile("\\p{Alnum}*");

  /**
   * One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
   */
  public static Pattern PUNCTUATION = Pattern.compile("\\p{Punct}*");

  /**
   * <p/> Postal codes: alphanumeric plus the dash and space. (Need alpha because they can be
   * international.) </p>
   */
  public static Pattern POSTAL_CODE = Pattern.compile("[\\p{Alnum}- ]*");

  /**
   * Country codes: 2, alphabetic characters -- no more, no less
   */
  public static Pattern COUNTRY_CODE = Pattern.compile("\\p{Alpha}{2}");

  /**
   * A pattern that forces the user to enter in a decimal point and two digits for cents.
   */
  public static Pattern MONEY = Pattern.compile("[0-9]+\\.[0-9][0-9]");

  /**
   * <p/>
   * Phone numbers: numeric plus dash, period, parenthesis </P>
   */
  public static Pattern PHONE_NUMBER = Pattern.compile("(\\+)?[0-9\\-\\.\\(\\) ]+(x[0-9 ]+)?");

  /**
   * <p/> A pattern for identifying legitimate Java classnames. These are simply alphabetic
   * characters delimited by periods. </p>
   */
  public static Pattern JAVA_CLASS = Pattern.compile("(\\p{Alpha}+\\.)*\\p{Alpha}+");

  /**
   * <p/> A pattern for identifying legitimate mime types. These are two sets of alpha numeric
   * characters (potentially with '-'s and '.'s delimited by a forward slash. </p>
   */
  public static Pattern MIME_TYPES = Pattern.compile("^[\\p{Alnum}-.]+\\/[\\p{Alnum}-.]+$");

  /**
   * <p/> A pattern for identifying license names, of the form D.DDD-DDDD. These must start with 3 --
   * those are, currently, the only series of tags that you can legitimately make licenses of. </p>
   */
  public static Pattern LICENSE_NAMES = Pattern.compile("3\\.[0-9]*\\-[0-9]*");

  /**
   * <p>A pattern for validating IP addresses.</p>
   */
  public static Pattern IP_ADDRESS = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

  /**
   * <p/> A pattern for identifying legitimate URLs. </p>
   */
  public static Pattern URL = Pattern.compile(
      "^(https?://)" + "(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" +
          // user@
          "(([0-9]{1,3}\\.){3}[0-9]{1,3}" +
          // IP- 199.194.52.184
          "|" +
          // allows either IP or domain
          "localhost" +
          // localhost is okay
          "|" +
          // allows either IP or domain
          "([0-9a-z_!~*'()-]+\\.)*" +
          // tertiary domain(s)- www.
          "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." +
          // second level domain
          "[a-z]{2,6})" +
          // first level domain- .com, .museum
          "(:[0-9]{1,4})?" +
          // port number- :80
          "((/?)|" +
          // a slash isn't required if there is no file name
          "(/[0-9a-z_!~*'\"//().;?:@&=+$,%#-|]+)+/?)$",
      // trailing filename
      Pattern.CASE_INSENSITIVE);

  /**
   * <p/> A pattern for identifying legitimate URLs. </p>
   */
  public static Pattern LOCALHOST = Pattern.compile("^(https?://)(127\\.0\\.0\\.1|localhost)(:[0-9]{1,4})?(/[0-9a-z_!~*'\"//().;?:@&=+$,%#-]+)*/?$", Pattern.CASE_INSENSITIVE);

  /**
   * A pattern for identifying a domain name in WHOIS dump
   */
  public static Pattern WHOIS_DOMAIN = Pattern.compile("^(\\s*Domain Name:\\s)([0-9a-z-]+\\.[a-z]{2,6})(\\s*)$", Pattern.CASE_INSENSITIVE);

  /**
   * A pattern for identifying a domain name in WHOIS dump
   */
  public static Pattern WHOIS_IP_ADDRESS = Pattern.compile("^(\\s*IP Address:\\s)(" + Patterns.IP_ADDRESS.pattern() + ")$", Pattern.CASE_INSENSITIVE);
  /**
   * A pattern for identifying a WHOIS server in WHOIS dump
   */
  public static Pattern WHOIS_SERVER = Pattern.compile("^(\\s*Whois Server:\\s)(whois\\.[0-9a-z-]+\\.[a-z]{2,6})(\\s*)$", Pattern.CASE_INSENSITIVE);

  /**
   * A pattern for identifying a legal search term. Currently just alphanumeric, spaces,
   * dashes, periods, and apostrophes
   */
  public static Pattern SEARCH_TERM = Pattern.compile("[\\p{Alnum}\\p{Blank}\\.\\-\']*");

  /**
   * A pattern for identifiying the index of an array element
   */
  public static Pattern INDEX_REFERENCE = Pattern.compile("\\[\\d+\\]");

  /**
   * A patter for identifying string of characters between double quotation marks
   */
  public static Pattern QUOTED_TEXT = Pattern.compile("\"([^\"]+)\"");

  /**
   * <p/> Do not instantiate Pattern objects. </p>
   */
  private Patterns() {
    // nop
  }

  /**
   * <p/> A simple helper function to return true if a candidate string matches one of our standard
   * patterns. This is good when a full field validation with length requirements and the like is not
   * required. </p>
   *
   * @param pat       The pattern to query
   * @param candidate The candidate string to consider
   * @return true if the candidate matches the pattern; false otherwise
   */
  public static boolean matches(Pattern pat, String candidate) {
    Validate.notNull(pat);
    if (candidate != null) {
      Matcher m = pat.matcher(candidate);
      return m.matches();
    } else {
      return false;
    }
  }

  /**
   * Compile a literal String into a regex Pattern
   *
   * @param phrase literal string
   * @param flags  regex pattern flags
   * @return regex pattern
   */
  public static Pattern compileLiteral(String phrase, int flags) {
    phrase = phrase.replace("\\", "\\\\"); // Double-quote existing backslashes
    phrase = phrase.replace("+", "\\+"); // plus sign
    phrase = phrase.replace("$", "\\$"); // dollar (end of string)
    phrase = phrase.replace("^", "\\^"); // caret(front of string)
    phrase = phrase.replace("*", "\\*"); // asterisk sign
    phrase = phrase.replace("?", "\\?"); // question mark
    phrase = phrase.replace("(", "\\("); // left parenthesis
    phrase = phrase.replace(")", "\\)"); // right parenthesis
    phrase = phrase.replace("[", "\\["); // left bracket
    phrase = phrase.replace("]", "\\]"); // right bracket
    phrase = phrase.replace("|", "\\|"); // vertical bar
    phrase = phrase.replace("{", "\\{"); // left squiggly
    phrase = phrase.replace("}", "\\{"); // right squiggly
    phrase = phrase.replace(" ", "\\s+"); // whitespace
    return Pattern.compile(phrase, flags);
  }
}
