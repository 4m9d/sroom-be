package com.m9d.sroom.object;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {

    private final EnrollCondition enrollCondition;

    private final List<ContentSaved> contentSavedList;

    @Getter
    private final List<CourseVideo> courseVideoList;

    public Course(EnrollCondition enrollCondition, ContentSaved contentSaved, List<CourseVideo> courseVideoList) {
        this.enrollCondition = enrollCondition;
        this.contentSavedList = new ArrayList<>(List.of(contentSaved));
        this.courseVideoList = courseVideoList;
    }

    public Integer getVideoCount() {
        return courseVideoList.size();
    }

    public String getTitle() {
        return contentSavedList.get(0).getTitle();
    }

    public String getThumbnail() {
        return contentSavedList.get(0).getThumbnail();
    }

    public int getDuration() {
        return courseVideoList.stream()
                .mapToInt(CourseVideo::getDuration)
                .sum();
    }

    public Date getExpectedEndDate() {
        return enrollCondition.getExpectedEndDate();
    }

    public Long getFirstSourceId() {
        return contentSavedList.get(0)
                .getId();
    }
}
