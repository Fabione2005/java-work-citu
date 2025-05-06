package com.citi.enroll.biometrics.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseExceptionHandlerTest {

    private BaseExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler  = new BaseExceptionHandler();
        request = mock(HttpServletRequest.class);
    }

    @Test
    void handleApiClientException_shouldReturnFatalErrorResponse() {
        // given a malformed JSON exception
        HttpMessageNotReadableException ex =
            new HttpMessageNotReadableException("bad payload", new RuntimeException());

        // when
        ErrorResponse resp = handler.handleApiClientException(request, ex);

        // then
        assertNotNull(resp, "ErrorResponse must not be null");
        assertEquals(ErrorResponse.TypeEnum.FATAL, resp.getType(), "Type should be FATAL");
        assertEquals("serverError",         resp.getCode(), "Code should be 'serverError'");
        assertEquals(
          "The request failed due to an internal error/server unavailability",
          resp.getMessage(),
          "Message should match the static text"
        );
    }

    @Test
    void handleThrowable_shouldReturnSameFatalErrorResponse() {
        // given any throwable
        Throwable t = new IllegalStateException("oops");

        // when
        ErrorResponse resp = handler.handleThrowable(request, t);

        // then
        assertNotNull(resp, "ErrorResponse must not be null");
        assertEquals(ErrorResponse.TypeEnum.FATAL, resp.getType(), "Type should be FATAL");
        assertEquals("serverError",         resp.getCode(), "Code should be 'serverError'");
        assertEquals(
          "The request failed due to an internal error/server unavailability",
          resp.getMessage(),
          "Message should match the static text"
        );
    }
}
