package com.dbeast.templates_generator.exceptions;

public class TemplateNotFoundException extends Exception {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param templateName the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TemplateNotFoundException(String templateName) {
        super(String.format("Error! Template for comparing: \"%s\" doesn't exists", templateName));
    }
}
