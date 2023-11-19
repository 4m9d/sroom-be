package com.m9d.sroom.material.model;

import com.m9d.sroom.ai.exception.QuizTypeNotMatchException;
import com.m9d.sroom.material.exception.MaterialTypeNotFoundException;

public enum MaterialType {

    SUMMARY(0),

    QUIZ(1);

    private final int value;

    MaterialType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MaterialType from(int contentTypeInt) {
        if (contentTypeInt == SUMMARY.getValue()) {
            return SUMMARY;
        } else if (contentTypeInt == QUIZ.getValue()) {
            return QUIZ;
        }
        return null;
    }

    public static MaterialType fromStr(String valueStr) {
        if (valueStr.equals("summary")) {
            return SUMMARY;
        } else if (valueStr.equals("quiz")) {
            return QUIZ;
        } else {
            throw new MaterialTypeNotFoundException(valueStr);
        }
    }

    public String toStr() {
        switch (this) {
            case SUMMARY:
                return "summary";
            case QUIZ:
                return "quiz";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
