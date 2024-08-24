package org.project.currencyexchange.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExchangeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExchangeCurrencyException.class)
    public final ResponseEntity<Object> handleExchangeCurrencyException(ExchangeCurrencyException exception){
        log.error("Error Occurred! {}, Error Message: {}", exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
        return buildErrorMessage(exception.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> handleEntityException(EntityNotFoundException exception) {
        log.error("Error Occurred! {}, Error Message: {}", exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
        return buildErrorMessage(ExchangeErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllUnhandledException(Exception exception) {
        log.error("Error Occurred! {}, Error Message: {}", exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
        return buildErrorMessage(ExchangeErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected final ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers,
                                                                        HttpStatusCode status, WebRequest request) {
        log.error("Error Occurred! {}, Error Message: {}", exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
        if (exception.getCause() instanceof InvalidFormatException e) {
            List<InvalidFieldInfo> details = new ArrayList<>();
            String fieldName = e.getPath().getFirst().getFieldName();
            String invalidValue = e.getValue().toString();
            String message = String.format("Invalid value '%s' for field '%s'", invalidValue, fieldName);
            InvalidFieldInfo invalidFieldInfo = new InvalidFieldInfo(fieldName, message, invalidValue);
            details.add(invalidFieldInfo);
            return buildErrorMessage(ExchangeErrorCode.BAD_REQUEST, details, HttpStatus.BAD_REQUEST);
        } else {
            return buildErrorMessage(ExchangeErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                        HttpHeaders headers, HttpStatusCode status,
                                                                        WebRequest request) {
        log.error("Error Occurred! {}, Error Message: {}", exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
        List<InvalidFieldInfo> details = new ArrayList<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> details.add(new InvalidFieldInfo(((FieldError) error).getField(),
                        error.getDefaultMessage(),
                        ObjectUtils.nullSafeToString(((FieldError) error).getRejectedValue()))));
        return buildErrorMessage(ExchangeErrorCode.BAD_REQUEST, details, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<Object> buildErrorMessage(ExchangeErrorCode exchangeErrorCode, HttpStatus status) {
        ExchangeErrorResponse response = ExchangeErrorResponse.builder()
                .errorCode(exchangeErrorCode.getCode()).errorMessage(exchangeErrorCode.getMessage())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Object> buildErrorMessage(ExchangeErrorCode exchangeErrorCode, List<InvalidFieldInfo> invalidFieldInfoList,
                                                     HttpStatus status) {
        ExchangeErrorResponse response = ExchangeErrorResponse.builder()
                .errorCode(exchangeErrorCode.getCode()).errorMessage(exchangeErrorCode.getMessage())
                .detail(new Detail(invalidFieldInfoList))
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
