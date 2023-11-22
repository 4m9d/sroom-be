package com.m9d.sroom.material.dto.request;

import lombok.Setter;

@Setter
public class FeedbackRequest {

    private Boolean is_satisfactory;

    public boolean isSatisfactory() {
        return is_satisfactory;
    }
}
