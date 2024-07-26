package com.knguyendev.api.exception;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * This exception class represents the structure for standardizing error responses in our application. All exceptions
 * will reach our 'ControllerAdvice' class will be transformed into an instance of this class, and then serialized when
 * the HTTP response is sent.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON serialization
public class ExceptionDetails {
    private String message;
    private HttpStatus httpStatus;
    private ZonedDateTime timestamp;

    /**
     * this field can be null if the error that we're handling is not a 'MethodArgumentNotValid' exception.
     */
    private Map<String, String> fieldErrors;
}