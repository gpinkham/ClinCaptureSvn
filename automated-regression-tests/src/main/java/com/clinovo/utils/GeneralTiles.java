package com.clinovo.utils;

/**
 * Created by Anton on 31.05.2014.
 */
public enum GeneralTiles {
    LEFT_PARENTHESIS("(", "Group"),
    RIGHT_PARENTHESIS(")", "Group"),

    LESS_THAN("<", "Compare"),
    GREATER_THAN(">", "Compare"),
    LESS_THAN_OR_EQUAL("<=", "Compare"),
    GREATER_THAN_OR_EQUAL(">=", "Compare"),
    EQUAL("=", "Compare"),
    NOT_EQUAL("!=", "Compare"),
    DOES_NOT_CONTAIN("!E", "Compare"),
    CONTAIN("E", "Compare"),

    PLUS("+", "Calculate"),
    MINUS("-", "Calculate"),
    MULTIPLY("*", "Calculate"),
    DIVIDE("/", "Calculate"),

    AND("AND", "Condition"),
    OR("OR", "Condition"),

    TEXT("Text", "Data"),
    NUMBER("Number", "Data"),
    DATE("Date", "Data"),

    CURRENT_DATE("Current date", "Macros"),
    EMPTY("Empty", "Macros")
    ;

    private GeneralTiles(String tileText, String section) {
    }
}
