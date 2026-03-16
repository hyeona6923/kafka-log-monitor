package io.github.hyuna.logmonitor.logging.util;

import io.github.hyuna.logmonitor.logging.enums.ErrorType;

public class ErrorClassifier {

    public static ErrorType classify(String message) {

        if (message == null) {
            return ErrorType.UNKNOWN;
        }

        String msg = message.toLowerCase();

        if (msg.contains("timeout")) {
            return ErrorType.TIMEOUT;
        }

        if (msg.contains("validation")) {
            return ErrorType.VALIDATION_ERROR;
        }

        if (msg.contains("db") || msg.contains("sql")) {
            return ErrorType.DB_ERROR;
        }

        if (msg.contains("connection") || msg.contains("network")) {
            return ErrorType.NETWORK_ERROR;
        }

        return ErrorType.UNKNOWN;
    }
}