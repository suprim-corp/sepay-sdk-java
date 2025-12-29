package suprim.sepay.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * API error response DTO for parsing error responses from SePay API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiErrorResponse {

    private String error;
    private String message;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
