package com.m9d.sroom.util;

import static com.m9d.sroom.lecture.constant.LectureConstant.*;

public class ValidateUtil {
    public static boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(LECTURE_CODE_START_INDEX, LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(PLAYLIST_CODE_INDICATOR);
    }
}
