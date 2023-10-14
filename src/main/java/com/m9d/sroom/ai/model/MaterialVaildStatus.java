package com.m9d.sroom.ai.model;

public enum MaterialVaildStatus {

    IN_VALID(0),

    VALID(1);

    private final int value;

    MaterialVaildStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
