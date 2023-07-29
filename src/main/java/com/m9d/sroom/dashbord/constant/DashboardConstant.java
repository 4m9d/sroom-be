package com.m9d.sroom.dashbord.constant;

import java.util.ArrayList;
import java.util.List;

public class DashboardConstant {

    //Motivation
    public static final String MOTIVATION_CONSECUTIVE_LEARNING = "일 연속으로 수강중이에요!";
    public static final String MOTIVATION_RESTART_LEARNING = "오늘부터 다시 시작해봐요!";
    public static final String MOTIVATION_TOTAL_LEARNING_TIME_PREFIX = "시간 더 들으시면 ";
    public static final String MOTIVATION_TOTAL_LEARNING_TIME_SUFFIX = "시간 달성!";
    public static final String MOTIVATION_INDUCE_REVIEW = "님의 후기를 공유해주세요!";
    public static final List<String> MOTIVATION_GENERAL;

    static {
        MOTIVATION_GENERAL = new ArrayList<>();
        MOTIVATION_GENERAL.add("이 문제 풀면 나랑 사귀는거다?");
        MOTIVATION_GENERAL.add("퀴즈하나 맛있게 말아봐...");
        MOTIVATION_GENERAL.add("경고 : 지나친 학습은 거북목을 유발할 수 있습니다.");
        MOTIVATION_GENERAL.add("어떤 영상을 좋아할지 몰라서 유튜브를 다 갖고왔어요 ^^");
        MOTIVATION_GENERAL.add("스룸 없는 시험기간? 생각도 하기 시룸");
    }

}
