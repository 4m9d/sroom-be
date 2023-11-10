package com.m9d.sroom.material.model;

public enum MaterialType {

    SUMMARY(0),

    QUIZ(1);

    private final int value;

    MaterialType(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static MaterialType from(int contentTypeInt){
        if(contentTypeInt == SUMMARY.getValue()){
            return SUMMARY;
        }else if(contentTypeInt == QUIZ.getValue()){
            return QUIZ;
        }
        return null;
    }
}
