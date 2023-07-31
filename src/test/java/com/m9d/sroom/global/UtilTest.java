package com.m9d.sroom.global;

import com.m9d.sroom.util.SroomTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UtilTest extends SroomTest {

    @Test
    @DisplayName("time 포멧변환이 성공적으로 이루어집니다.")
    void convertTimeFormat() throws Exception {
        //given
        String onlySecond = "0:11";
        String minuteSingle = "5:23";
        String minuteDouble = "14:09";
        String hourSingle = "5:09:44";
        String hourDouble = "11:03:52";

        //when
        Long onlySecondToSecond = dateUtil.convertTimeToSeconds(onlySecond);
        Long minuteSingleToSecond = dateUtil.convertTimeToSeconds(minuteSingle);
        Long minuteDoubleToSecond = dateUtil.convertTimeToSeconds(minuteDouble);
        Long hourSingleToSecond = dateUtil.convertTimeToSeconds(hourSingle);
        Long hourDoubleToSecond = dateUtil.convertTimeToSeconds(hourDouble);

        //then
        Assertions.assertEquals(onlySecondToSecond, 11L);
        Assertions.assertEquals(minuteSingleToSecond, 323L);
        Assertions.assertEquals(minuteDoubleToSecond, 849L);
        Assertions.assertEquals(hourSingleToSecond, 18584L);
        Assertions.assertEquals(hourDoubleToSecond, 39832L);
    }
}
