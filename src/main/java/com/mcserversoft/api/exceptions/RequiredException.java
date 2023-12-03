package com.mcserversoft.api.exceptions;

import java.util.List;

public class RequiredException extends Exception {

    private List<String> requiredFields;

    public RequiredException(String message, List<String> requiredFields) {
        super(message);
        this.requiredFields = requiredFields;
    }

    public String[] getRequiredFields() {
        return this.requiredFields.toArray(new String[0]);
    }

    public String getRequiredFieldsString() {
        StringBuilder sb = new StringBuilder();
        for (String field : this.requiredFields) {
            sb.append(field).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
