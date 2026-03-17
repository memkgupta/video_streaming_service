package com.vsnt.asset_onboarding.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStateException extends APIException {

    private final String currentState;
    private final String action;

    public InvalidStateException(String currentState, String action) {
        super(
                String.format(
                        "Cannot perform action '%s' when resource is in state '%s'",
                        action,
                        currentState
                ),
                HttpStatus.CONFLICT

        );
        this.currentState = currentState;
        this.action = action;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getAction() {
        return action;
    }
}