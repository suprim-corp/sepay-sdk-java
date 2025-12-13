package suprim.sepay.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void defaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertNotNull(response);
    }

    @Test
    void setAndGetSuccess() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        assertTrue(response.isSuccess());

        response.setSuccess(false);
        assertFalse(response.isSuccess());
    }

    @Test
    void setAndGetData() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData("test data");
        assertEquals("test data", response.getData());
    }

    @Test
    void setAndGetError() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setError("VALIDATION_ERROR");
        assertEquals("VALIDATION_ERROR", response.getError());
    }

    @Test
    void setAndGetMessage() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Test message");
        assertEquals("Test message", response.getMessage());
    }

    @Test
    void dataWithObject() {
        ApiResponse<Object> response = new ApiResponse<>();
        Object data = new Object();
        response.setData(data);
        assertSame(data, response.getData());
    }
}
