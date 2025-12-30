package suprim.sepay.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * Pattern matching utility for extracting identifiers from transaction content.
 *
 * <p>Example: Pattern "SE" matches "SE123456" in "Thanh toan SE123456"
 * and extracts identifier "123456".
 *
 * <p>Usage:
 * <pre>
 * PatternMatcher matcher = new PatternMatcher();
 * Optional&lt;String&gt; id = matcher.extractIdentifier("Thanh toan SE123456", "SE");
 * // Returns Optional.of("123456")
 * </pre>
 */
public class PatternMatcher {

    /**
     * Match pattern in content and extract identifier.
     *
     * @param content Transaction content
     * @param patternPrefix Pattern prefix (e.g., "SE")
     * @return Extracted identifier if match found, empty otherwise
     */
    public Optional<String> extractIdentifier(String content, String patternPrefix) {
        if (isNull(content) || content.isEmpty() || isNull(patternPrefix) || patternPrefix.isEmpty()) {
            return Optional.empty();
        }

        // Build regex: pattern followed by alphanumeric identifier
        // Pattern must not be preceded or followed by letters (to avoid matching inside words)
        String regex = Pattern.quote(patternPrefix) + "([a-zA-Z0-9-_]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        // Find first valid match where pattern is not part of a larger word
        while (matcher.find()) {
            int start = matcher.start();
            int patternEnd = start + patternPrefix.length();

            // Check if pattern prefix is preceded by a letter (would make it part of a word like "BASE")
            boolean precedesLetter = start > 0 && Character.isLetter(content.charAt(start - 1));

            // Check if pattern prefix itself ends with a letter AND is followed by a letter
            // (would make "SEASON" match "SE", but "ID123ABC" can match "ID123")
            char lastCharOfPattern = patternPrefix.charAt(patternPrefix.length() - 1);
            boolean followedByLetter = Character.isLetter(lastCharOfPattern) &&
                                      patternEnd < content.length() &&
                                      Character.isLetter(content.charAt(patternEnd));

            if (!precedesLetter && !followedByLetter) {
                String fullMatch = matcher.group(0);
                // Remove prefix to get identifier
                String identifier = fullMatch.substring(patternPrefix.length());
                return Optional.of(identifier);
            }
        }

        return Optional.empty();
    }
}
