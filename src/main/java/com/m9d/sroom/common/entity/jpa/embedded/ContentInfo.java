package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ContentInfo {

    private String title;

    private String channel;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String thumbnail;

    private Boolean isAvailable;

    private Integer duration;

    private Timestamp publishedAt;
}
