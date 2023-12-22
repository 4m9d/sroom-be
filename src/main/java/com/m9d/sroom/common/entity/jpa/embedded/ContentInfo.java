package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
