package main.presentation.exception;

import main.presentation.dto.ResultDto;
import main.presentation.dto.ResultWithErrorsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<HashMap<String, String>> handleBadRequestException(BadRequestException e) {
        HashMap<String, String> message = new HashMap<>();
        message.put("message", e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FalseResultException.class)
    public ResponseEntity<ResultDto> handleFalseResultExceptions(FalseResultException e) {
        if (e.getErrors() == null) {
            return new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResultWithErrorsDto(false, e.getErrors()), HttpStatus.OK);
    }
}