package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.Scheduling;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "COURSE")
@Getter
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private String courseTitle;

    private Integer courseDuration;

    @UpdateTimestamp
    private Timestamp lastViewTime;

    private Integer progress;

    @Column(columnDefinition = "text")
    private String thumbnail;

    @Embedded
    private Scheduling scheduling;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @OneToMany(mappedBy = "course")
    private List<CourseVideoEntity> courseVideos = new ArrayList<CourseVideoEntity>();
}
