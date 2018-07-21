package com.systematictesting.media;

public class AbortException extends Exception {

    public static final long serialVersionUID = 1L;

    public AbortException() {
        super();
    }

    public AbortException(String message) {
        super(message);
    }
}
