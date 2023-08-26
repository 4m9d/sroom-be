package com.m9d.sroom.material.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Material {

    private int status;

    private int quizNum;

    private List<Quiz> quizzes;

    private Summary summary;
}
