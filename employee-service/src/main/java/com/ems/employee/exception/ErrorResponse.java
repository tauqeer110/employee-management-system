package com.ems.employee.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/*
  Standard error response structure returned to the client on exceptions.
  To keep error responses consistent across all endpoints.
 */
@Getter
public class ErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
