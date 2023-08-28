package com.m9d.sroom.global;

import com.google.gson.Gson;
import com.m9d.sroom.util.SroomTest;
import com.m9d.sroom.util.youtube.resource.SearchReq;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

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

    @Test
    @DisplayName("객체의 필드객체도 매핑이 되는지 확인합니다.")
    void mappingObject() throws Exception {
        String jsonStr = "{\n" +
                "  \"nextPageToken\": \"CAoQAA\",\n" +
                "  \"pageInfo\": {\n" +
                "    \"totalResults\": 1000000,\n" +
                "    \"resultsPerPage\": 10\n" +
                "  },\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#playlist\",\n" +
                "        \"playlistId\": \"PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"네트워크 기초(개정판)\",\n" +
                "        \"description\": \"OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"따라하면서 배우는 IT\",\n" +
                "        \"publishTime\": \"2020-01-02T13:15:51Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#playlist\",\n" +
                "        \"playlistId\": \"PLXvgR_grOs1BFH-TuqFsfHqbh-gpMbFoy\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"네트워크 기초 이론\",\n" +
                "        \"description\": \"\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/k1gyh9BlOT8/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/k1gyh9BlOT8/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/k1gyh9BlOT8/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"널널한 개발자 TV\",\n" +
                "        \"publishTime\": \"2022-03-01T12:41:22Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"Av9UFzl_wis\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"[따라學IT] 01. 네트워크란 무엇인가?\",\n" +
                "        \"description\": \"\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"따라하면서 배우는 IT\",\n" +
                "        \"publishTime\": \"2020-08-29T05:35:37Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"6l7xP7AnB64\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"[입문용] 프로토콜과 OSI 7 layer 설명! 네트워크의 기능들이 어떻게 구조화 돼서 동작하는지를 설명합니다! \uD83D\uDC4D\",\n" +
                "        \"description\": \"프로토콜 #OSI모델 #네트워크 #쉬운코드 #백발백중 네트워크에서 통신을 하기 위해서는 프로토콜이 필요합니다 그리고 그 프로토콜 ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/6l7xP7AnB64/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/6l7xP7AnB64/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/6l7xP7AnB64/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"쉬운코드\",\n" +
                "        \"publishTime\": \"2023-03-27T13:00:08Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"65h9uxHKGPk\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"☆ 쉬움주의) 네트워크 기초 기술 모르시는 분들 이 정도만 알면 기술사만큼 됩니다. 책한권 뚝딱 누구나 알면 좋을 컴퓨터 상식\",\n" +
                "        \"description\": \"네트워크#기초#기술#컴퓨터#기술사 네트워크 기술 쉽게 설명 드려봅니다! 해당 도서 구매 링크 https://link.coupang.com/a/PA9VQ ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/65h9uxHKGPk/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/65h9uxHKGPk/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/65h9uxHKGPk/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"기술노트with 알렉\",\n" +
                "        \"publishTime\": \"2020-01-05T15:08:21Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"WdsZGu8gcvY\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"윈도우 인터넷 문제 네트워크 초기화로 완벽히 해결 해 봅시다\",\n" +
                "        \"description\": \"모바일랩 채널 가입 ♥ https://bit.ly/38XzZfO ※ 코인만랩 채널 : https://bit.ly/3Eerz39 ※ 젤리초코 채널 ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/WdsZGu8gcvY/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/WdsZGu8gcvY/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/WdsZGu8gcvY/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"모바일랩\",\n" +
                "        \"publishTime\": \"2023-07-02T02:18:28Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"GmxPT-P331s\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"“느려”… 스튜디오 네트워크 속도 답답해서 10기가 NAS 만들기 도전해봤습니다?\",\n" +
                "        \"description\": \"이전에 10Gbps 에드온 카드를 시놀로지 NAS에 연결해서 속도를 뚫어줬습니다. 이후 문제는 해당 랜카드는 포트가 2개 뿐이라 2대의 ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/GmxPT-P331s/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/GmxPT-P331s/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/GmxPT-P331s/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"ITSub잇섭\",\n" +
                "        \"publishTime\": \"2023-01-11T11:00:14Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"ZBy1FZBpFrk\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"윈도우10 꿀팁 네트워크 연결이 안될때 네트워크 공유 진짜 쉽게하기 네트워크설정 프린터찾기 진짜 컴퓨터기사가 알려주는 네트워크 설정 방법\",\n" +
                "        \"description\": \"네트워크연결이안될때 #네트워크공유 #네트워크 #네트워크설정 #윈도우10꿀팁 네트워크 연결이 안될때 윈도우10 꿀팁 네트워크 ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ZBy1FZBpFrk/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ZBy1FZBpFrk/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ZBy1FZBpFrk/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"곰손장인-TV\",\n" +
                "        \"publishTime\": \"2020-08-24T22:27:19Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"ihIWWWf4NNU\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"[코인리더][마스크네트워크코인]머스크도하고 저커버그도 하는? 마스크네트워크|오랜만에 나온 반등| SNS와 웹3 두 마리 토끼 다 잡았다! #마스크네트워크호재 #-이프로\",\n" +
                "        \"description\": \"코인 정보 어플 간편설치링크▽ https://coinreader.co.kr/ 코인을 처음시작하는 초보자, 중수, 고수 모든분들에게 필요한 정보를 빠르게 ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ihIWWWf4NNU/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ihIWWWf4NNU/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/ihIWWWf4NNU/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"코인리더\",\n" +
                "        \"publishTime\": \"2023-07-31T08:50:54Z\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": {\n" +
                "        \"kind\": \"youtube#video\",\n" +
                "        \"videoId\": \"K9L9YZhEjC0\"\n" +
                "      },\n" +
                "      \"snippet\": {\n" +
                "        \"title\": \"이해하면 인생이 바뀌는 TCP 송/수신 원리\",\n" +
                "        \"description\": \"소켓 프로그램 개발자, 운영체제 개발(혹은 튜닝) 가능자, 네트워크 관리자...이 세 가지 관점을 모두 이해하면 알 수 있는 TCP Segment ...\",\n" +
                "        \"thumbnails\": {\n" +
                "          \"default\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/K9L9YZhEjC0/default.jpg\",\n" +
                "            \"width\": 120,\n" +
                "            \"height\": 90\n" +
                "          },\n" +
                "          \"medium\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/K9L9YZhEjC0/mqdefault.jpg\",\n" +
                "            \"width\": 320,\n" +
                "            \"height\": 180\n" +
                "          },\n" +
                "          \"high\": {\n" +
                "            \"url\": \"https://i.ytimg.com/vi/K9L9YZhEjC0/hqdefault.jpg\",\n" +
                "            \"width\": 480,\n" +
                "            \"height\": 360\n" +
                "          }\n" +
                "        },\n" +
                "        \"channelTitle\": \"널널한 개발자 TV\",\n" +
                "        \"publishTime\": \"2022-03-20T11:31:53Z\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();


        SearchVo searchVo = gson.fromJson(jsonStr, SearchVo.class);

        System.out.println(searchVo.toString());

    }

    @Test
    @DisplayName("web client non-block을 테스트합니다.")
    void testNonBlocking() throws Exception {
        SearchReq lectureListReq = SearchReq.builder()
                .keyword("네트워크")
                .filter("all")
                .limit(10)
                .pageToken(null)
                .build();
        System.out.println(System.currentTimeMillis());
        Mono<SearchVo> test = youtubeApi.getSearchVo(lectureListReq);
        System.out.println(System.currentTimeMillis());
        SearchVo searchVo = youtubeUtil.safeGetVo(test);
        System.out.println(System.currentTimeMillis());
        System.out.println(searchVo.toString());
        System.out.println(System.currentTimeMillis());
    }
}
