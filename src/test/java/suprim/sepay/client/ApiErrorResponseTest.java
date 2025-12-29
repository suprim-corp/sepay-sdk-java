package suprim.sepay.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    void defaultConstructor() {
        ApiErrorResponse response = new ApiErrorResponse();
        assertNotNull(response);
        assertNull(response.getError());
        assertNull(response.getMessage());
    }

    @Test
    void parameterizedConstructor() {
        ApiErrorResponse response = new ApiErrorResponse("ERROR_CODE", "Error message");
        assertEquals("ERROR_CODE", response.getError());
        assertEquals("Error message", response.getMessage());
    }

    @Test
    void setAndGetError() {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setError("VALIDATION_ERROR");
        assertEquals("VALIDATION_ERROR", response.getError());
    }

    @Test
    void setAndGetMessage() {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage("Test error message");
        assertEquals("Test error message", response.getMessage());
    }
}
