package com.m9d.sroom.util;


import com.m9d.sroom.search.constant.SearchConstant;

public class ValidateUtil {

    public static boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(SearchConstant.LECTURE_CODE_START_INDEX,
                SearchConstant.LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(SearchConstant.PLAYLIST_CODE_INDICATOR);
    }
}
