package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.common.entity.jpa.CourseQuizEntity;
import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.material.model.MaterialStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Material {

    private int status;

    private int totalQuizCount;

    private List<QuizResponse> quizzes;

    private SummaryBrief summaryBrief;

    public static Material ofCreating() {
        return Material.builder()
                .status(MaterialStatus.CREATING.getValue())
                .build();
    }

    public static Material ofCreationFailed() {
        return Material.builder()
                .status(MaterialStatus.CREATION_FAILED.getValue())
                .build();
    }
}
