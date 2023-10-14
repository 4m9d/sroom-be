package com.m9d.sroom.common.repository.coursequiz;


import com.m9d.sroom.common.dto.CourseQuizDto;

import java.util.Optional;

public interface CourseQuizRepository {

    CourseQuizDto save(CourseQuizDto courseQuizDto);

    CourseQuizDto getById(Long courseQuizId);

    Optional<CourseQuizDto> findById(Long courseQuizId);

    CourseQuizDto updateById(Long courseQuizId, CourseQuizDto courseQuizDto);

    Optional<CourseQuizDto> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId);

    void deleteByCourseId(Long courseId);
}
