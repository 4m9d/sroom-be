package com.m9d.sroom.materialFeedback;

import com.m9d.sroom.common.entity.jpa.MaterialFeedbackEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.VideoEntity;
import com.m9d.sroom.material.model.MaterialType;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.AssertTrue;

public class MaterialFeedbackRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("피드백 저장에 성공합니다.")
    void saveFeedback() {
        //given
        MemberEntity member = getMemberEntity();

        //when
        MaterialFeedbackEntity feedbackEntity = feedbackRepository.save(MaterialFeedbackEntity.create(member,
                1L, MaterialType.QUIZ.getValue(), false));

        //then
        Assertions.assertNotNull(feedbackEntity.getFeedbackId());
        Assertions.assertEquals(feedbackRepository.getById(1L).getContentType(),
                MaterialType.QUIZ.getValue());
    }
}
