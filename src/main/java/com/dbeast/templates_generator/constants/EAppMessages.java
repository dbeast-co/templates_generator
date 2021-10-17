package com.dbeast.templates_generator.constants;

public enum EAppMessages {
    ON_CHANGE_LOG_MESSAGE("FIELD TYPE CHANGED! Field: '%s' changed from %s to '%s'"),
    ON_TYPE_CONFLICT_LOG_MESSAGE("FIELDS TYPE CONFLICT! Field: '%s' have generated type: '%s' and template type: '%s'.");
    private final String message;

    EAppMessages(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
