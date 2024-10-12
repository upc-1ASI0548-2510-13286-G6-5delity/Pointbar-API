package com.metasoft.pointbarmetasoft.shared.exception;
import com.metasoft.pointbarmetasoft.shared.exception.dto.ErrorMessageResponse;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    public ApiResponse<ErrorMessageResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest
    ){
        var errorMessageResponse= new ErrorMessageResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );
        log.error(exception.getMessage());
        return new ApiResponse<>(false, exception.getMessage(), errorMessageResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception,
            WebRequest webRequest
    ) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return new ApiResponse<>(false, "Validation errors", errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<ErrorMessageResponse> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest webRequest
    ) {
        var errorMessage = new ErrorMessageResponse(
                LocalDateTime.now(),
                "You do not have permission to access this resource.",
                webRequest.getDescription(false)
        );

        log.error("Access denied: {}", exception.getMessage());
        return new ApiResponse<>(false, "Access denied", errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<ErrorMessageResponse> handleGlobalException(
            Exception exception,
            WebRequest webRequest
    ) {
        if (exception instanceof AccessDeniedException) {
            log.error("Access denied exception: {}", exception.getMessage());
            throw (AccessDeniedException) exception; // Permite que Spring maneje 403 de forma predeterminada
        }

        var errorMessage = new ErrorMessageResponse(
                LocalDateTime.now(),
                "An unexpected error occurred. Please contact support if the issue persists.",  // Mensaje genérico
                webRequest.getDescription(false) // No reveles los detalles del error
        );

        log.error("Internal server error: {}", exception.getMessage());
        return new ApiResponse<>(false, "Internal server error", errorMessage);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<ErrorMessageResponse>> handleApplicationException(
            ApplicationException exception,
            WebRequest webRequest
    ){
        var errorMessageResponse= new ErrorMessageResponse(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false)
        );
        log.error(exception.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(false, exception.getMessage(), errorMessageResponse), exception.getHttpStatus());
    }
}
