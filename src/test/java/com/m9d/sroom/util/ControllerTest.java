package com.m9d.sroom.util;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ControllerTest extends SroomTest {

    public Login getNewLogin() {
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);
        return login;
    }

    protected Member getNewMember() {
        UUID uuid = UUID.randomUUID();

        String memberCode = uuid.toString();
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

    protected KeywordSearch getKeywordSearch(Login login, String keyword) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        return objectMapper.readValue(jsonContent, KeywordSearch.class);
    }

    protected PlaylistDetail getPlaylistDetail(Login login, String playlistCode) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures/{lectureCode}", playlistCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        System.out.println(jsonContent);
        return objectMapper.readValue(jsonContent, PlaylistDetail.class);
    }

    protected Long enrollNewCourseWithVideo(Login login) {
        Object obj = jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId");
        Long memberId = Long.valueOf((String) obj);

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(VIDEO_CODE)
                .build();
        EnrolledCourseInfo courseId = courseService.enrollCourse(memberId, newLecture, false);
        return courseId.getCourseId();
    }


    @Test
    @DisplayName("객체의 필드객체도 매핑이 되는지 확인합니다.")
    void mappingObject() throws Exception {
        String jsonStr = "{\n" +
                "  \"result_per_page\": 10,\n" +
                "  \"next_page_token\": \"CAoQAA\",\n" +
                "  \"prev_page_token\": null,\n" +
                "  \"lectures\": [\n" +
                "    {\n" +
                "      \"lecture_title\": \"네트워크 기초(개정판)\",\n" +
                "      \"description\": \"OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.\",\n" +
                "      \"channel\": \"따라하면서 배우는 IT\",\n" +
                "      \"lecture_code\": \"PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/Av9UFzl_wis/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": true,\n" +
                "      \"is_playlist\": true,\n" +
                "      \"view_count\": 0,\n" +
                "      \"lecture_count\": 8,\n" +
                "      \"published_at\": \"2023-07-27 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"네트워크 기초 이론\",\n" +
                "      \"description\": \"\",\n" +
                "      \"channel\": \"널널한 개발자 TV\",\n" +
                "      \"lecture_code\": \"PLXvgR_grOs1BFH-TuqFsfHqbh-gpMbFoy\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/k1gyh9BlOT8/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": true,\n" +
                "      \"view_count\": 0,\n" +
                "      \"lecture_count\": 8,\n" +
                "      \"published_at\": \"2021-02-27 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"윈도우10 꿀팁 네트워크 연결이 안될때 네트워크 공유 진짜 쉽게하기 네트워크설정 프린터찾기 진짜 컴퓨터기사가 알려주는 네트워크 설정 방법\",\n" +
                "      \"description\": \"네트워크연결이안될때 #네트워크공유 #네트워크 #네트워크설정 #윈도우10꿀팁 네트워크 연결이 안될때 윈도우10 꿀팁 네트워크 ...\",\n" +
                "      \"channel\": \"곰손장인-TV\",\n" +
                "      \"lecture_code\": \"ZBy1FZBpFrk\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/ZBy1FZBpFrk/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 123,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2022-05-23 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"누구나 3만원으로 홈 네트워크 만들기, 인터넷 기사님이 안해주는 이유\",\n" +
                "      \"description\": \"종종 에어플레이를 지원하는데 연결이 안된다거나, 홈팟을 스테레오로 쓸 수가 없다거나 하는 문제들 겪으시는 분들 많으시죠?\",\n" +
                "      \"channel\": \"서울리안 SEOULiAN\",\n" +
                "      \"lecture_code\": \"FxGQ1le-Si8\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/FxGQ1le-Si8/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 29487,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2012-11-21 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"☆ 쉬움주의) 네트워크 기초 기술 모르시는 분들 이 정도만 알면 기술사만큼 됩니다. 책한권 뚝딱 누구나 알면 좋을 컴퓨터 상식\",\n" +
                "      \"description\": \"네트워크#기초#기술#컴퓨터#기술사 네트워크 기술 쉽게 설명 드려봅니다! 해당 도서 구매 링크 https://link.coupang.com/a/PA9VQ ...\",\n" +
                "      \"channel\": \"기술노트with 알렉\",\n" +
                "      \"lecture_code\": \"65h9uxHKGPk\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/65h9uxHKGPk/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 23,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2023-07-30 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"NAD CS1 국내 출시! 차이파이 가격으로 즐기는 하이파이 #네트워크 #플레이어 #스트리머 #하이파이 #오디오  추천\",\n" +
                "      \"description\": \"앙증맞은 크기와 심플한 인터페이스에도 불구하고 고성능 DAC을 내장하고 강력한 스트리밍 기능 까지 갖춘 NAD CS1! 소리샵 청담 ...\",\n" +
                "      \"channel\": \"소리샵 Sorishop_TUBE\",\n" +
                "      \"lecture_code\": \"j8icUtaRc1E\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/j8icUtaRc1E/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 1212233,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2002-05-03 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"[따라學IT] 01. 네트워크란 무엇인가?\",\n" +
                "      \"description\": \"\",\n" +
                "      \"channel\": \"따라하면서 배우는 IT\",\n" +
                "      \"lecture_code\": \"Av9UFzl_wis\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/Av9UFzl_wis/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": true,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 10003254,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2022-04-17 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"“느려”… 스튜디오 네트워크 속도 답답해서 10기가 NAS 만들기 도전해봤습니다?\",\n" +
                "      \"description\": \"이전에 10Gbps 에드온 카드를 시놀로지 NAS에 연결해서 속도를 뚫어줬습니다. 이후 문제는 해당 랜카드는 포트가 2개 뿐이라 2대의 ...\",\n" +
                "      \"channel\": \"ITSub잇섭\",\n" +
                "      \"lecture_code\": \"GmxPT-P331s\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/GmxPT-P331s/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 12345,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2021-01-23 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"이해하면 인생이 바뀌는 TCP 송/수신 원리\",\n" +
                "      \"description\": \"소켓 프로그램 개발자, 운영체제 개발(혹은 튜닝) 가능자, 네트워크 관리자...이 세 가지 관점을 모두 이해하면 알 수 있는 TCP Segment ...\",\n" +
                "      \"channel\": \"널널한 개발자 TV\",\n" +
                "      \"lecture_code\": \"K9L9YZhEjC0\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/K9L9YZhEjC0/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 0,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2023-07-23 10:30:21\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"lecture_title\": \"영업 비밀 노출) 서버 네트워크 데이터베이스 이정도만 알면 \uD83D\uDE0E\",\n" +
                "      \"description\": \"서버#네트워크#데이터베이스 서버 네트워크 데이터 베이스에 대해서 설명드려봅니다. 멤버쉽 가입하시면 IT분야 진로 상담, 앱개발 ...\",\n" +
                "      \"channel\": \"기술노트with 알렉\",\n" +
                "      \"lecture_code\": \"Pc6n6HgWU5c\",\n" +
                "      \"rating\": 0.0,\n" +
                "      \"review_count\": 0,\n" +
                "      \"thumbnail\": \"https://i.ytimg.com/vi/Pc6n6HgWU5c/mqdefault.jpg\",\n" +
                "      \"is_enrolled\": false,\n" +
                "      \"is_playlist\": false,\n" +
                "      \"view_count\": 504,\n" +
                "      \"lecture_count\": 1,\n" +
                "      \"published_at\": \"2022-12-21 10:30:21\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();

        KeywordSearch keywordSearch;

        keywordSearch = gson.fromJson(jsonStr, KeywordSearch.class);

        System.out.println(keywordSearch.toString());

    }

    @Test
    @DisplayName("web client non-block을 테스트합니다.")
    void testNonBlocking() throws Exception {
        LectureListReq lectureListReq = LectureListReq.builder()
                .keyword("네트워크")
                .filter("all")
                .limit(10)
                .pageToken(null)
                .build();
        System.out.println(System.currentTimeMillis());
        Mono<String> testStr = youtubeApi.getYoutubeVoStr(lectureListReq);
        System.out.println(System.currentTimeMillis());
        String test = testStr.block();
        System.out.println(System.currentTimeMillis());
        Gson gson = new Gson();
        SearchVo searchVo = gson.fromJson(test, SearchVo.class); //예외처리 추가
        System.out.println(System.currentTimeMillis());

        System.out.println(searchVo);
    }
}
