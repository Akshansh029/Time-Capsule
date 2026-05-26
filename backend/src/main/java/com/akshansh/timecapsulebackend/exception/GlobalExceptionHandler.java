package com.akshansh.timecapsulebackend.exception;

import com.akshansh.timecapsulebackend.model.dto.ErrorResponse;
import com.akshansh.timecapsulebackend.model.dto.ValidationErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed of incorrect field types or values",
                errors
        );
        log.warn("Client error event=fieldValidationFailed status=404 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=resourceNotFound status=404 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CapsuleAlreadyUnlockedException.class)
    public ResponseEntity<ErrorResponse> handleCapsuleAlreadyUnlocked(CapsuleAlreadyUnlockedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=capsuleAlreadyUnlocked status=409 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthCode(InvalidAuthCodeException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=invalidAuthCode status=401 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "User already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=userAlreadyExists status=400 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ErrorResponse> handleWrongPassword(WrongPasswordException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid email or password",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=wrongPassword status=401 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnumValue(InvalidEnumValueException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Provided value is not an enum value",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=invalidEnumValue status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthentication(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid email or password",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=invalidAuthentication status=401 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT Exception",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=jwtException status=401 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=validationFailed status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Unauthorized action",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=actionForbidden status=403 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleHeaderNotFound(MissingRequestHeaderException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Invalid required request header",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=headerNotFound status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                "Access denied: You do not have permission to access this resource.",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=accessDenied status=400 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnlockDatePassedException.class)
    public ResponseEntity<ErrorResponse> handlePassedUnlockDate(UnlockDatePassedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Invalid Unlock date",
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=unlockDatePassed status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=invalidRequest status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidEx && invalidEx.getTargetType().isEnum()) {
            ErrorResponse error = getErrorResponse(request, invalidEx);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        // Fallback for other JSON parse errors
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Bad Request", ex.getMessage(), request.getRequestURI());
        log.warn("Client error event=httpMessageNotReadable status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private static @NonNull ErrorResponse getErrorResponse(HttpServletRequest request, InvalidFormatException invalidEx) {
        String message = String.format("Invalid value '%s' for field '%s'. Must be one of %s.",
                invalidEx.getValue(),
                invalidEx.getPath().getLast().getPropertyName(),
                Arrays.toString(invalidEx.getTargetType().getEnumConstants())
        );
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), message, request.getRequestURI());
        return error;
    }

    @ExceptionHandler(SpringBootFileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUpload(SpringBootFileUploadException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=fileUploadError status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<ErrorResponse> handleFileEmpty(FileEmptyException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=emptyFile status=400 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<ErrorResponse> handleFileDownload(FileDownloadException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.error("Server error event=fileDownload status=500 method={} uri={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidVerificationCode.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCode(InvalidVerificationCode ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.warn("Client error event=invalidVerificationCode status=400 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResendEmailException.class)
    public ResponseEntity<ErrorResponse> handleResendEmail(ResendEmailException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to send email",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.error("Server error event=runtimeError status=500 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        log.error("Server error event=unexpectedError status=500 method={} uri={} userId={} errorType={} message=\"{}\" requestId={}",
                request.getMethod(),
                request.getRequestURI(),
                MDC.get("userId"),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                MDC.get("requestId")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
