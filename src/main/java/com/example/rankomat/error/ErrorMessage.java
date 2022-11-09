package com.example.rankomat.error;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.*;

@Value
@Builder
public class ErrorMessage {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    LocalDateTime timestamp;
    int code;
    String status;
    String message;
    String uri;
    String method;
}
