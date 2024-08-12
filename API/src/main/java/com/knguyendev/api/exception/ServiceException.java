package com.knguyendev.api.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception class used for service layer errors in the application.
 * <p>
 * This exception is thrown to indicate that a service layer operation has failed due to some business logic error,
 * such as validation issues or other domain-specific problems. The exception includes an HTTP status code to indicate
 * the nature of the error more precisely.
 * <p>
 * You'd raise this exception whilst inside a service method.
 * Example usage:
 * <pre>
 *     Bool userAlreadyExists = userRepo.findByUsername(username);
 *     if (userAlreadyExists) {
 *         throw new ServiceException("Username already taken!", HttpStatus.BAD_REQUEST);
 *     }
 * </pre>
 *
 *
 * @see RuntimeException
 */
@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus httpStatus;
    public ServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
