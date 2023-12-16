package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.MemberStats;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
@DynamicInsert
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String memberCode;

    private String memberName;

    private String refreshToken;

    @Embedded
    private MemberStats stats;

    @CreationTimestamp
    private Timestamp signUpTime;

    private Boolean status;

    private String bio;

    @OneToMany(mappedBy = "member")
    private List<CourseEntity> courses = new ArrayList<CourseEntity>();

    public Integer countCompletedCourse() {
        return (int) courses.stream()
                .filter(courseEntity -> courseEntity.getProgress().equals(100))
                .count();
    }

    @Builder
    public MemberEntity(String memberCode, String memberName) {
        this.memberCode = memberCode;
        this.memberName = memberName;
    }

    public void updateName(String newName) {
        this.memberName = newName;
    }
}
