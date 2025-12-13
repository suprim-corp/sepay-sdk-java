package suprim.sepay.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PatternMatcher.
 */
class PatternMatcherTest {

    private final PatternMatcher matcher = new PatternMatcher();

    @Test
    void testExtract_simpleMatch() {
        Optional<String> result = matcher.extractIdentifier(
            "Thanh toan SE123456",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
    }

    @Test
    void testExtract_withUnderscoreHyphen() {
        Optional<String> result = matcher.extractIdentifier(
            "Payment SE_abc-def-123",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("_abc-def-123", result.get());
    }

    @Test
    void testExtract_wordBoundary() {
        // "SEASON" should NOT match "SE" due to word boundary
        Optional<String> result = matcher.extractIdentifier(
            "Happy SEASON greetings",
            "SE"
        );
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_noMatch() {
        Optional<String> result = matcher.extractIdentifier(
            "No pattern here",
            "SE"
        );
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_multipleMatches() {
        // Should return first match
        Optional<String> result = matcher.extractIdentifier(
            "SE123 and SE456",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_customPattern() {
        Optional<String> result = matcher.extractIdentifier(
            "Order ACME9999 paid",
            "ACME"
        );
        assertTrue(result.isPresent());
        assertEquals("9999", result.get());
    }

    @Test
    void testExtract_nullContent() {
        Optional<String> result = matcher.extractIdentifier(null, "SE");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_nullPattern() {
        Optional<String> result = matcher.extractIdentifier("SE123", null);
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_emptyContent() {
        Optional<String> result = matcher.extractIdentifier("", "SE");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_emptyPattern() {
        Optional<String> result = matcher.extractIdentifier("SE123", "");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_patternAtStart() {
        Optional<String> result = matcher.extractIdentifier(
            "SE123456 Thanh toan",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
    }

    @Test
    void testExtract_patternAtEnd() {
        Optional<String> result = matcher.extractIdentifier(
            "Thanh toan SE123456",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
    }

    @Test
    void testExtract_alphanumericIdentifier() {
        Optional<String> result = matcher.extractIdentifier(
            "Payment SE123abc456XYZ",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123abc456XYZ", result.get());
    }

    @Test
    void testExtract_patternWithNumbers() {
        Optional<String> result = matcher.extractIdentifier(
            "Order ID123ABC paid",
            "ID123"
        );
        assertTrue(result.isPresent());
        assertEquals("ABC", result.get());
    }

    @Test
    void testExtract_longerPattern() {
        Optional<String> result = matcher.extractIdentifier(
            "Transaction ORDER123456 completed",
            "ORDER"
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
    }

    @Test
    void testExtract_noIdentifierAfterPattern() {
        // Pattern exists but no identifier follows
        Optional<String> result = matcher.extractIdentifier(
            "SE only",
            "SE"
        );
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_patternInMiddleOfWord() {
        // "BASE123" should not match "SE" because SE is not at word boundary
        Optional<String> result = matcher.extractIdentifier(
            "BASE123 transaction",
            "SE"
        );
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_vietnameseContent() {
        Optional<String> result = matcher.extractIdentifier(
            "Chuyen khoan SE123456 thanh cong",
            "SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
    }

    // Security tests

    @Test
    void testExtract_regexSpecialCharsInPattern() {
        // Pattern with regex special characters should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Order SE.123456 test",
            "SE."
        );
        assertTrue(result.isPresent());
        assertEquals("123456", result.get());

        // Without proper escaping, "SE." would match "SEA" in "SEAL123"
        result = matcher.extractIdentifier(
            "SEAL123 test",
            "SE."
        );
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_patternWithBrackets() {
        // Test regex special chars are properly escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test [SE]123 data",
            "[SE]"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithBackslash() {
        // Backslash in pattern should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Path SE\\123 test",
            "SE\\"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithDollar() {
        // Dollar sign should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Price $SE123 test",
            "$SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithCaret() {
        // Caret should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test ^SE123 data",
            "^SE"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithStar() {
        // Asterisk should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test SE*123 data",
            "SE*"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithPlus() {
        // Plus should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test SE+123 data",
            "SE+"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithQuestion() {
        // Question mark should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test SE?123 data",
            "SE?"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithPipe() {
        // Pipe should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test SE|123 data",
            "SE|"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithParentheses() {
        // Parentheses should be escaped
        Optional<String> result = matcher.extractIdentifier(
            "Test (SE)123 data",
            "(SE)"
        );
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_longContent_noReDoS() {
        // Test that long content doesn't cause ReDoS
        String longContent = "A".repeat(10000) + " SE123456 " + "B".repeat(10000);
        long startTime = System.currentTimeMillis();

        Optional<String> result = matcher.extractIdentifier(longContent, "SE");

        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.isPresent());
        assertEquals("123456", result.get());
        assertTrue(duration < 1000, "Pattern matching should complete in under 1 second");
    }

    @Test
    void testExtract_manyPatternOccurrences_noReDoS() {
        // Test with many potential matches
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("SE").append(i).append(" ");
        }

        long startTime = System.currentTimeMillis();

        Optional<String> result = matcher.extractIdentifier(content.toString(), "SE");

        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.isPresent());
        assertEquals("0", result.get()); // First match
        assertTrue(duration < 1000, "Pattern matching should complete in under 1 second");
    }

    @Test
    void testExtract_repeatingChars_noReDoS() {
        // Patterns that could cause catastrophic backtracking
        // Using space before SE to ensure word boundary is satisfied
        String content = "1".repeat(50) + " SE123";

        long startTime = System.currentTimeMillis();

        Optional<String> result = matcher.extractIdentifier(content, "SE");

        long duration = System.currentTimeMillis() - startTime;

        assertTrue(result.isPresent());
        assertEquals("123", result.get());
        assertTrue(duration < 1000, "Should complete quickly even with repeating chars");
    }

    @Test
    void testExtract_nestedPatternLike_noReDoS() {
        // Pattern that looks like it could cause nested backtracking
        String content = "SESESESESE123456";

        long startTime = System.currentTimeMillis();

        Optional<String> result = matcher.extractIdentifier(content, "SE");

        long duration = System.currentTimeMillis() - startTime;

        // Should match first valid SE followed by identifier
        assertTrue(duration < 1000, "Should complete quickly");
    }

    @Test
    void testExtract_whitespaceOnly() {
        Optional<String> result = matcher.extractIdentifier("   ", "SE");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_patternOnly() {
        // Content is exactly the pattern with no identifier
        Optional<String> result = matcher.extractIdentifier("SE", "SE");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_contentShorterThanPattern() {
        Optional<String> result = matcher.extractIdentifier("S", "SE");
        assertFalse(result.isPresent());
    }

    @Test
    void testExtract_patternAtEndOfContent() {
        // Pattern ends exactly at content end - tests branch coverage
        Optional<String> result = matcher.extractIdentifier("Order SE123", "SE");
        assertTrue(result.isPresent());
        assertEquals("123", result.get());
    }

    @Test
    void testExtract_patternWithLetterAtEndOfContent() {
        // Pattern ends with letter exactly at content end - tests followedByLetter=false when patternEnd==content.length()
        Optional<String> result = matcher.extractIdentifier("SE", "SE");
        assertFalse(result.isPresent()); // No identifier after pattern
    }
}
