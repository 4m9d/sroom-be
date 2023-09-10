package com.m9d.sroom.material.model;

public enum MaterialStatus {
    NO_REQUEST(-2),
    CREATING(0),
    CREATED(1),
    CREATION_FAILED(-1);

    private final int value;

    MaterialStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
