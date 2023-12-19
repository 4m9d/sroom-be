package com.m9d.sroom.courseQuiz;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.util.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CourseQuizRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("채점 결과를 저장하는데 성공합니다.")
    void saveCourseQuiz() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
    }
}
