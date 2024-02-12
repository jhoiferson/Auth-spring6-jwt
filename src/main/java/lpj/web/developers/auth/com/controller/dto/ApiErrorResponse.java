package lpj.web.developers.auth.com.controller.dto;

import lombok.Data;

@Data
public class ApiErrorResponse
{
	private int statusCode;
	private String message;
	private String errorType;
	private String errorCode;

	// Constructor, getters y setters

    public ApiErrorResponse(int statusCode, String message, String errorType, String errorCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

    // Getters y setters
    
}
