package com.m9d.sroom.material.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {

    @JsonProperty("is_satisfactory")
    private Boolean is_satisfactory;

    public boolean isSatisfactory() {
        return is_satisfactory;
    }
}
