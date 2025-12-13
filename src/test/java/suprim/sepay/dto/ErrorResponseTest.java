package suprim.sepay.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponse DTO.
 */
class ErrorResponseTest {

    @Test
    void testInvalidToken() {
        ErrorResponse response = ErrorResponse.invalidToken();

        assertEquals("INVALID_TOKEN", response.getError());
        assertEquals("Authorization token is invalid or missing", response.getMessage());
    }

    @Test
    void testDuplicateTransaction() {
        ErrorResponse response = ErrorResponse.duplicateTransaction();

        assertEquals("DUPLICATE_TRANSACTION", response.getError());
        assertEquals("Transaction already processed", response.getMessage());
    }

    @Test
    void testValidationError_customMessage() {
        ErrorResponse response = ErrorResponse.validationError("Amount must be positive");

        assertEquals("VALIDATION_ERROR", response.getError());
        assertEquals("Amount must be positive", response.getMessage());
    }

    @Test
    void testValidationError_emptyMessage() {
        ErrorResponse response = ErrorResponse.validationError("");

        assertEquals("VALIDATION_ERROR", response.getError());
        assertEquals("", response.getMessage());
    }

    @Test
    void testValidationError_nullMessage() {
        ErrorResponse response = ErrorResponse.validationError(null);

        assertEquals("VALIDATION_ERROR", response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void testFactoryMethods_returnNewInstances() {
        ErrorResponse first = ErrorResponse.invalidToken();
        ErrorResponse second = ErrorResponse.invalidToken();

        // Should be equal in content but different objects
        assertNotSame(first, second);
        assertEquals(first.getError(), second.getError());
        assertEquals(first.getMessage(), second.getMessage());
    }

    @Test
    void testGetters_immutable() {
        ErrorResponse response = ErrorResponse.duplicateTransaction();

        // Ensure getters return consistent values
        String error1 = response.getError();
        String error2 = response.getError();
        String message1 = response.getMessage();
        String message2 = response.getMessage();

        assertEquals(error1, error2);
        assertEquals(message1, message2);
    }

    @Test
    void testValidationError_longMessage() {
        String longMessage = "A".repeat(500);
        ErrorResponse response = ErrorResponse.validationError(longMessage);

        assertEquals("VALIDATION_ERROR", response.getError());
        assertEquals(longMessage, response.getMessage());
        assertEquals(500, response.getMessage().length());
    }

    @Test
    void testValidationError_vietnameseMessage() {
        String vietnameseMessage = "So tien khong hop le";
        ErrorResponse response = ErrorResponse.validationError(vietnameseMessage);

        assertEquals("VALIDATION_ERROR", response.getError());
        assertEquals(vietnameseMessage, response.getMessage());
    }

    @Test
    void testValidationError_specialCharacters() {
        String specialMessage = "Error: {\"field\": \"amount\", \"value\": -100}";
        ErrorResponse response = ErrorResponse.validationError(specialMessage);

        assertEquals("VALIDATION_ERROR", response.getError());
        assertEquals(specialMessage, response.getMessage());
    }
}
