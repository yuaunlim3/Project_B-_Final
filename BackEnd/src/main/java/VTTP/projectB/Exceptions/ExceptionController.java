package VTTP.projectB.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import VTTP.projectB.Models.Exceptions.ApiError;
import VTTP.projectB.Models.Exceptions.DuplicateItemException;
import VTTP.projectB.Models.Exceptions.ExistedAccountException;
import VTTP.projectB.Models.Exceptions.FailedPaymentException;
import VTTP.projectB.Models.Exceptions.InvalidCodeException;
import VTTP.projectB.Models.Exceptions.InvalidPasswordException;
import VTTP.projectB.Models.Exceptions.InvalidUserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiError> handleException(InvalidUserException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ApiError apiError = new ApiError(404, ex.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleException(InvalidPasswordException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ApiError apiError = new ApiError(401, ex.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExistedAccountException.class)
    public ResponseEntity<ApiError> handleException(ExistedAccountException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ApiError apiError = new ApiError(409, ex.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FailedPaymentException.class)
    public ResponseEntity<ApiError> handleException(FailedPaymentException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ApiError apiError = new ApiError(400, ex.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<ApiError> handleException(InvalidCodeException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ApiError apiError = new ApiError(401, ex.getMessage());
        return new ResponseEntity<ApiError>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateItemException.class)
    public ResponseEntity<ApiError> handleDuplicateItemException(DuplicateItemException ex) {
        ApiError apiError = new ApiError(409, ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

}
