package tools;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final Logger LOGGER = Logger.getLogger(StringUtil.class.getCanonicalName());

    public static final int DEFAULT_SUMMARY_LENGTH = 200;

    public static String ALPHACHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Truncates a string if it exceeds length
     * 
     * @param text
     *            - string to be truncated
     * @param boundary
     *            - position of truncate
     * @return - a truncated version of text
     */
    public static String truncate(String text, String endWith, int boundary) {

        text = trimToEmpty(text);
        endWith = trimToEmpty(endWith);

        if (text.length() > boundary) {
            text = text.substring(0, boundary) + endWith;
        }
        return text;
    }

    /**
     * Trims a string to the nearest sentence break at the specified length.
     * 
     * @param text
     *            - string to be trimmed
     * @param boundary
     *            - specified length for trimming
     * @return a trimmed version of text
     */
    public static String trimAtSentenceBoundaries(String text, String endWith, int boundary) {

        text = trimToEmpty(text);
        endWith = trimToEmpty(endWith);

        BreakIterator bi = BreakIterator.getSentenceInstance();
        bi.setText(text);
        int currentIndex = 0, preSentBreakpoint = 0;
        while (bi.next() != BreakIterator.DONE && boundary > currentIndex) {
            preSentBreakpoint = currentIndex;
            currentIndex = bi.current();
        }
        if (boundary - preSentBreakpoint < currentIndex - boundary && preSentBreakpoint != 0) {
            currentIndex = preSentBreakpoint;
        }
        if (text.length() <= currentIndex) {
            endWith = "";
        }
        text = text.substring(0, currentIndex) + endWith;

        return text;
    }

    /**
     * Trims a string to the nearest sentence break at the specified length.
     * 
     * @param text
     *            - string to be trimmed
     * @param boundary
     *            - specified length for trimming
     * @return a trimmed version of text
     */
    public static String trimAtWordBefore(String text, String endWith, int boundary) {
        endWith = trimToEmpty(endWith);
        text = trimToEmpty(text);
        if (text.length() <= boundary)
            return text;

        BreakIterator bi = BreakIterator.getWordInstance(Locale.CANADA_FRENCH);
        bi.setText(text);

        int lastIndex = bi.preceding(boundary);
        if (lastIndex == BreakIterator.DONE) {
            return text;
        }
        return trimToEmpty(text.substring(0, lastIndex)) + endWith;
    }

    public static String trimAfterWord(String text, String endWith, int boundary) {
        endWith = trimToEmpty(endWith);
        text = trimToEmpty(text);
        if (text.length() <= boundary)
            return text;

        BreakIterator bi = BreakIterator.getWordInstance(Locale.CANADA_FRENCH);
        bi.setText(text);

        int lastIndex = bi.following(boundary);
        if (lastIndex == BreakIterator.DONE) {
            return text;
        }
        return trimToEmpty(text.substring(0, lastIndex)) + endWith;
    }

    public static String trimHTMLAfterWord(String text, String endWith, int boundary) {
        String s = trimHTMLText(text);

        return trimAfterWord(s, endWith, boundary);
    }

    public static String trimHTMLAtWordBefore(String text, String endWith, int boundary) {
        String s = trimHTMLText(text);

        return trimAtWordBefore(s, endWith, boundary);
    }

    public static boolean isEmpty(String text) {
        if (text == null) {
            return true;
        } else {
            return text.trim().isEmpty();
        }
    }

    /**
     * Trims a text entered in a fckeditor so "
     * <p>
     * &nbsp; &nbsp;
     * </p>
     * " is trimmed to ""
     * 
     * @param text
     *            - string to be trimmed
     * @return a html trimmed version of text
     */
    public static String trimHTMLText(String text) {
        String ret = "";
        if (text != null) {
            ret = text.replaceAll("\\<.*?\\>", "");
            ret = ret.replaceAll("&nbsp;", "");
            ret = ret.trim();
        } else {
            ret = "";
        }
        return ret;
    }

    public static String escapeDoubleQuote(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replaceAll("\"", "\\\\\"");
        }
        return s;
    }

    public static int forceIntegerFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer getIntegerFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public static String escapeJs(String s) {
        return StringEscapeUtils.escapeEcmaScript(s);
    }

    public static String unAccent(String s) {
        String nfdNormalizedString = Normalizer.normalize(StringUtils.trimToEmpty(s), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static String getUTF8(String s) {
        if (s != null) {
            return new String(s.getBytes(), Charset.forName("UTF-8"));
        } else {
            return s;
        }
    }

    public static String removeDoubleSpace(String in) {

        // White space removal and replacement
        String out = in;
        if (out != null) {
            while (out.indexOf("  ") != -1) {
                out = out.replace("  ", " ");
            }
        }

        return out;
    }

    public static String normalizeStrict(String s) {
        return Normalizer.normalize(trimToEmpty(s), Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase();
    }

    public static String normalizeForUrl(String s) {

        String ret = trimToEmpty(s);

        ret = unAccent(ret);

        ret = getAlphaNumeric(ret, "?!.:,;' ", "");

        ret = getAlphaNumeric(ret, "", " ");

        ret = removeDoubleSpace(ret).trim();

        ret = getAlphaNumeric(ret, "", "-");

        return ret.toLowerCase();
    }

    /**
     * Removes any non alphanumeric characters from input string
     * 
     * 
     * @param input
     *            String in which we want to remove non alphanumeric characters
     * @return
     */

    public static String getAlphaNumeric(String input) {
        return getAlphaNumeric(input, "", "");
    }

    /**
     * Removes any non alphanumeric characters from input string and gives the
     * possibility to extends the list of of allowed characters
     * 
     * @param input
     *            String in which we want to remove characters
     * @param allowedCharactersExtension
     *            other characters allowed in the string
     * @return
     */
    public static String getAlphaNumeric(String input, String allowedCharactersExtension) {
        return getAlphaNumeric(input, allowedCharactersExtension, "");
    }

    /**
     * Removes any non alphanumeric characters from input string and gives the
     * possibility to extends the list of of allowed characters as well as to
     * specify by what String the characters should be replaced with
     * 
     * @param input
     *            String in which we want to remove characters
     * 
     * @param allowedCharactersExtension
     *            other characters allowed in the string
     * @param replaceWith
     *            what to replaces the invalid characters with
     * 
     * @return
     */

    public static String getAlphaNumeric(String input, String allowedCharactersExtension, String replaceWith) {
        String out = "";
        if (input != null) {

            input = unAccent(input).toLowerCase();

            String allowedCharacters = "abcdefghijklmnopqrstuvwxyz0123456789" + allowedCharactersExtension;

            for (int i = 0; i < input.length(); i++) {
                if (allowedCharacters.indexOf(input.charAt(i)) != -1) {
                    out += input.charAt(i);
                } else {
                    out += replaceWith;
                }
            }
        }
        return out;
    }

    public static String getMd5(String s) {
        return getMd5(s, "");
    }

    public static String getMd5(String s, String seed) {

        String ret = "";
        String se = s + seed;

        byte[] defaultBytes = se.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            ret = hexString + "";
        } catch (Exception e) {

        }
        return ret;
    }

    public static String decodeUrl(String s) {

        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }

    public static boolean wildcardMatch(String text, String pattern) {
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.

        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx != 0) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }
        return true;
    }

    public static boolean wildcardMatch(String text, Iterable<String> patterns) {
        for (String patten : patterns) {
            if (StringUtil.wildcardMatch(text, patten)) {
                LOGGER.info("Matching found between " + text + " with patten " + patten);
                return true;
            }
        }
        return false;
    }

}
