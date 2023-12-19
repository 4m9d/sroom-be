package com.m9d.sroom.review;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.LectureEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.ReviewEntity;
import com.m9d.sroom.common.repository.review.ReviewJpaRepository;
import com.m9d.sroom.util.RepositoryTest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class reviewRepositoryTest extends RepositoryTest {

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Test
    @DisplayName("리뷰 등록에 성공합니다.")
    void saveReview() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);

        LectureEntity lecture = LectureEntity.create(course, 1L, false, 1, "channel1");
        lectureRepository.save(lecture);

        //when
        String sourceCode = "ABC";
        reviewRepository.save(ReviewEntity.create(lecture, sourceCode, 4, "좋아요!"));

        //then
        Assertions.assertEquals(reviewRepository.getById(1L).getSourceCode(), sourceCode);
    }

    @Test
    @DisplayName("특정 lecture의 모든 리뷰를 불러옵니다.")
    void getReviewList() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);

        LectureEntity lecture1 = LectureEntity.create(course, 1L, false, 1, "channel1");
        LectureEntity lecture2 = LectureEntity.create(course, 1L, false, 2, "channel1");
        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);

        String sourceCode = "ABC";
        reviewRepository.save(ReviewEntity.create(lecture1, sourceCode, 4, "좋아요!"));
        reviewRepository.save(ReviewEntity.create(lecture1, sourceCode, 5, "정말 좋아요!"));

        //when
        List<ReviewEntity> reviews = reviewRepository.getListByCode(sourceCode, 0 , 5);

        //then
        Assertions.assertEquals(reviews.get(0).getSubmittedRating(), 4);
        Assertions.assertEquals(reviews.get(1).getSubmittedRating(), 5);
    }
}
