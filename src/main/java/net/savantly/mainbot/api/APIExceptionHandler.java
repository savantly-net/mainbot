package net.savantly.mainbot.api;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.api.exceptions.ErrorDto;

@ControllerAdvice
@Slf4j
public class APIExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorDto> generateNotFoundException(ResponseStatusException ex) {
    ErrorDto errorDTO = new ErrorDto();
    errorDTO.setMessage(ex.getMessage());
    errorDTO.setStatus(ex.getStatusCode());
    errorDTO.setTime(new Date());

    return new ResponseEntity<ErrorDto>(errorDTO, errorDTO.getStatus());
  }


  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorDto> generateClientErrorException(IllegalArgumentException ex) {
    ErrorDto errorDTO = new ErrorDto();
    errorDTO.setMessage(ex.getMessage());
    errorDTO.setStatus(HttpStatus.BAD_REQUEST);
    errorDTO.setTime(new Date());

    log.debug("handling client error", ex);

    return new ResponseEntity<ErrorDto>(errorDTO, errorDTO.getStatus());
  }
}