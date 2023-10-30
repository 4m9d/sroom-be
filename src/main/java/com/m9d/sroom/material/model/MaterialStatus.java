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

    public static MaterialStatus from(long summaryId) {
        if (summaryId == CREATING.getValue()) {
            return MaterialStatus.CREATING;
        } else if (summaryId == CREATION_FAILED.getValue()) {
            return MaterialStatus.CREATION_FAILED;
        } else {
            return MaterialStatus.CREATED;
        }
    }
}
