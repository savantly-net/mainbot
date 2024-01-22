package net.savantly.mainbot.api.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ErrorDto {
    
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date time;
    private HttpStatusCode status;
}
