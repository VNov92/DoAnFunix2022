package com.vuntfx17196.global;

public class BadRequestAlertException extends RuntimeException {
    private String message;

    public BadRequestAlertException() {
    }

    public BadRequestAlertException(String message) {
        super(message);
        this.message = message;
    }
}
