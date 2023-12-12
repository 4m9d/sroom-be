package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "LECTURE")
public class LectureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lectureId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    //source_id의 경우 두 테이블(video, playlist) 모두 매핑되어야 하는데 이게 불가능해서 따로 연관관계를 설정하지 않았습니다.
    @Column(nullable = true)
    private Long sourceId;

    @Column(nullable = false)
    private Boolean playlist;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer lectureIndex;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean reviewed;

    @Column(nullable = false)
    private String channel;
}
