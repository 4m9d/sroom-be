package com.m9d.sroom.util;


import com.m9d.sroom.lecture.constant.LectureConstant;

public class ValidateUtil {

    public static boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(LectureConstant.LECTURE_CODE_START_INDEX,
                LectureConstant.LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(LectureConstant.PLAYLIST_CODE_INDICATOR);
    }
}
