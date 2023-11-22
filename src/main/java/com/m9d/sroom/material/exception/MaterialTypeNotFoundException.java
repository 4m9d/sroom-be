package com.m9d.sroom.material.exception;

import com.m9d.sroom.common.error.NotFoundException;

public class MaterialTypeNotFoundException extends NotFoundException {

    private static final String MESSAGE = "강의자료 타입을 찾을 수 없습니다. 입력받은 타입 : ";

    public MaterialTypeNotFoundException(String type) {
        super(MESSAGE + type);
    }
}
