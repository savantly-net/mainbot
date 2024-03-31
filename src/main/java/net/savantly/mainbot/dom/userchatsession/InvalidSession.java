package net.savantly.mainbot.dom.userchatsession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.experimental.StandardException;

@StandardException
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid Session")
public class InvalidSession extends RuntimeException {
    
}
