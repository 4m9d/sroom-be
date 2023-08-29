package com.m9d.sroom.material.model;

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

}
