//: guru.sfg.mssc.beer.service.web.controller.MvcControllerExceptionHandler.java


package guru.sfg.mssc.beer.service.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;


@Slf4j
@ControllerAdvice
public class MvcControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> methodArgumaneValidationErrorHandler(
            MethodArgumentNotValidException e) {

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errors = allErrors.stream()
                .map(oe -> String.join(" ", ((FieldError) oe).getField(),
                        oe.getDefaultMessage()))
                .collect(toUnmodifiableList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> modelValidationErrorHandler(
            ConstraintViolationException cve) {

        Set<ConstraintViolation<?>> allErrors = cve.getConstraintViolations();

        List<String> errMsg = allErrors.stream()
                .map(cv -> String.join(" ",
                        cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(toList());

        return new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List> handleBindException(BindException ex) {
        return new ResponseEntity(ex.getAllErrors(), HttpStatus.BAD_REQUEST);
    }

}///:~