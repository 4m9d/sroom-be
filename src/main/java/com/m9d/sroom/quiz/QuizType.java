package com.m9d.sroom.quiz;

public enum QuizType {

    MULTIPLE_CHOICE(1),

    SUBJECTIVE(2),

    TRUE_FALSE(3);

    private final int value;

    QuizType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QuizType fromValue(int value) {
        for (QuizType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid QuizType value: " + value);
    }

}
