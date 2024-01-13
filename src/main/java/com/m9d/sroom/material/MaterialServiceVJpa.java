package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.CourseQuizEntity;
import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.model.MaterialStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialServiceVJpa {

    public Material getMaterials(CourseVideoEntity courseVideoEntity) {
        MaterialStatus status = courseVideoEntity.getMaterialStatus();

        if (status.equals(MaterialStatus.CREATING)) {
            return Material.ofCreating();
        } else if (status.equals(MaterialStatus.CREATION_FAILED)) {
            return Material.ofCreationFailed();
        } else {
            return MaterialMapper.getMaterial(courseVideoEntity);
        }
    }

    public Material4PdfResponse getCourseMaterials(CourseEntity courseEntity) {
        if (courseEntity.hasUnpreparedMaterial()) {
            return Material4PdfResponse.createUnprepared(courseEntity.getCourseTitle(),
                    courseEntity.getCourseVideos().size());
        }

        return MaterialMapper.getCourseMaterials(courseEntity);
    }
}
