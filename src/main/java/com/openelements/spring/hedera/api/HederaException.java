package com.openelements.spring.hedera.api;

/**
 * Represents an exception that occurred while interacting with the Hedera network.
 */
public class HederaException extends Exception {

    /**
     * Constructs a new HederaException with the specified detail message.
     * @param message The detail message.
     */
    public HederaException(String message) {
        super(message);
    }

    /**
     * Constructs a new HederaException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause.
     */
    public HederaException(String message, Throwable cause) {
        super(message, cause);
    }
}
