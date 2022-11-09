package com.example.rankomat.error;

public class RecordIsAlreadyProcessed extends RuntimeException {
    public RecordIsAlreadyProcessed(String message) {
        super(message);
    }
}
