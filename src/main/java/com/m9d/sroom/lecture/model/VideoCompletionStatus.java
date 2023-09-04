package com.m9d.sroom.lecture.model;

public enum VideoCompletionStatus {

    COMPLETED_PREVIOUSLY(true),

    COMPLETED_NOW(true),

    INCOMPLETE(false),

    REWOUND_FROM_COMPLETE(true),

    REWOUND_FROM_INCOMPLETE(false);

    private final boolean value;

    VideoCompletionStatus(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
