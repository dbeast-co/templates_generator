package com.dbeast.templates_generator.exceptions;

public class IndexNotFoundOrEmptyException extends Exception {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param indexName the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IndexNotFoundOrEmptyException(String indexName) {
        super(String.format("Error! Index for analyze: \"%s\" not found or empty.", indexName));
    }
}
