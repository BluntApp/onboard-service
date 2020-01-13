package com.blunt.onboard.error;

import com.blunt.onboard.util.BluntConstant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class BluntExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BluntException.class)
  public final ResponseEntity<Object> handleBluntException(BluntException exception,
      WebRequest request) {
    logger.error(exception.getMessage());
    BluntError bluntError = prepareBluntError(exception);
    return new ResponseEntity<Object>(bluntError, exception.getStatus());
  }

  @Override
  protected ResponseEntity<Object>
  handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    BluntError bluntError = prepareBluntError(ex);
    return new ResponseEntity<>(bluntError, headers, status);

  }

  private BluntError prepareBluntError(Exception ex) {
    BluntError bluntError = new BluntError();
    bluntError.setTimestamp(LocalDateTime.now());
    bluntError.setMessage(ex.getMessage());
    if (ex instanceof MethodArgumentNotValidException) {
      fillValidationError((MethodArgumentNotValidException) ex, bluntError);
    }
    return bluntError;
  }

  private void fillValidationError(MethodArgumentNotValidException ex, BluntError bluntError) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(x -> x.getDefaultMessage())
        .collect(Collectors.toList());
    bluntError.setMessage(BluntConstant.VALIDATION_ERROR);
    bluntError.setErrors(errors);
  }


}
