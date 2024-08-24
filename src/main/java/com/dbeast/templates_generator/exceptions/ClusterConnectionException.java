package com.dbeast.templates_generator.exceptions;

public class ClusterConnectionException extends Exception{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param host The hostname of the cluster for connection. The detail message is saved for
     *             later retrieval by the {@link #getMessage()} method.
     * @param exception Original response exception
     */
    public ClusterConnectionException(String host, Exception exception) {
        super(String.format("Error! Can't connect to the cluster. Host '%s'. ", host) + "Exception: " + exception.getMessage());
    }
}
