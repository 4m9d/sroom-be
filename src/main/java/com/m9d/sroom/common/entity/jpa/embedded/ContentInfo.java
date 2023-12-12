package com.m9d.sroom.common.entity.jpa.embedded;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
public class ContentInfo {

    private String title;

    private String channel;

    private String description;

    private String thumbnail;

    private Boolean available;

    private Integer duration;

    private Timestamp publishedAt;
}
