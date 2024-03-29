# 생성형 AI를 활용한 유튜브 강의강의 플랫폼, '스룸' BackEnd Repo

- 배포 URL : https://www.sroom.kr
- 스룸 Organization : https://github.com/4m9d
- FrontEnd Repo : https://github.com/4m9d/sroom-fe
- AI Repo : https://github.com/4m9d/sroom-ai

<br>

## 프로젝트 소개

- **스룸**은 유튜브 강의를 효과적으로 공부하게 돕는 서비스입니다.

- <U>유튜브의 모든 컨텐츠</U>에 접근 가능하며, 나만의 강의 코스를 생성할 수 있습니다.
- 각 강의마다 자막을 기반으로 한 <U>강의노트와 퀴즈</U>를 제공합니다.
- 강의코스의 모든 <U>강의자료를 PDF로 다운</U>받아 공부할 수 있습니다.
- 가장 인기있는강의와 함께, 주제별로 재미있는 <U>강의를 추천</U>받을 수 있습니다.

<br>

#### 개발 기간 : 2023.05 ~

#### 개발 인원 : 3명 
|분야|개발자|
|---|---|
|BackEnd| [정두원](https://github.com/D-w-nJ), [손경식](https://github.com/Son-GyeongSik)|
|AI|[손경식](https://github.com/Son-GyeongSik)|
|FrontEnd|[이종준](https://github.com/oikkoikk)|

<br>

## **Stack**
    Java : 11
    SpringBoot : 2.3.8
    JPA : 2.3.8
    MariaDB : 3.0.8
    Docker : 24.0.2
    ehcache : 2.10.6
    logstash : 7.2
    swagger : 3.0.0
    h2 : 1.4.200
    okhttp : 4.10.0
    AWS ECS, ECR, KMS, RDS

<br>

## 개발 방식

#### API 버전관리
- [SwaggerHub](https://app.swaggerhub.com/apis/sroom/api.sroom.com/2.0.0)
- yaml 파일로 작성, 현재 2.0.0 ver


#### 스크럼 - 2주 단위 스프린트
- Jira 칸반보드 사용 

- Confluence 사용한 스프린트 플래닝, 데일리 스크럼, KPT 회고, 백로그 작성


#### 이슈 및 PR 생성
- [이슈 템플릿](https://github.com/4m9d/sroom-be/blob/main/.github/ISSUE_TEMPLATE.md)에 따라 개요, 이유, 세부사항 작성

- [PR 템플릿](https://github.com/4m9d/sroom-be/blob/main/.github/PULL_REQUEST_TEMPLATE.md)에 따라 Motivation, Key changes, To reviewers 작성


#### MockAPI 사용
- Postman MockAPI 사용

- 프론트엔드 팀원에게 원활한 개발환경을 제공


#### 이슈관리
- Slack의 스레드 생성해 논의

- 필요하다면 백로그 추가


#### 테스트 서버 구축
- 프로덕션 환경과 같은 환경으로 테스트 서버 구축

- Pull Request 할 시 테스트서버 반영하여 디버깅


#### 브랜치 전략
- Github-flow 방식 채택

- 생성된 지라이슈 티켓으로 브랜치 생성, 개발 완료시 Main으로 PR, Merge


#### 테스트 코드 작성
- 기능개발 전, 테스트코드를 먼저 작성

- Service, Repository, Entity 계층의 유닛 테스트 코드 작성, API 단위의 통합테스트 작성

- 기능구현 후 테스트 요구사항 만족했는지 쉽게 확인 가능, 리팩토링 시 기존기능이 잘 작동하는지 쉽게 확인 가능했음

- 총 76개의 테스트 코드 작성


<br>

## 주요 개발 사항


|날짜|타입|개발 사항|내용|담당|
|---|---|---|---|---|
|2023-06-27|API 개발|유튜브 컨텐츠 검색기능 구현| - Youtube Data API(3rd-party API) 사용|[정두원](https://github.com/D-w-nJ)|
|2023-07-06|기능개발|구글 소셜 로그인|https://github.com/4m9d/sroom-be/pull/14 <br> - google id token, jwt 사용 <br> - accessToken, refreshToken 발급|[정두원](https://github.com/D-w-nJ)|
|2023-07-24|리팩토링|현직자 코드리뷰, 학습내용 적용|https://github.com/4m9d/sroom-be/pull/32 <br> - 테스트DB H2사용 <br> - SQL문은 Groovy 클래스에 선언 <br> - 상태코드 및 서비스 계층 문자열은 모두 상수처리 <br> - 서버에러 발생시 로그생성 <br> - 무분별한 트랜잭션 어노테이션 수정 <br> - 클린코드 스터디 적용(적절한 함수명, 변수명)|[정두원](https://github.com/D-w-nJ)|
|2023-08-03|성능개선|http라이브러리 성능비교|https://github.com/4m9d/sroom-be/pull/51<br> - Youtube Data API 사용하여 키워드검색, 상세검색하는 인터페이스 구현 <br> - 자바 http 라이브러리인 HttpUrlConnection, OkHttpClient, WebClient 사용하는 클래스 구현, 성능비교|[정두원](https://github.com/D-w-nJ)|
|2023-09-10|API개발|chatGPT사용 AI서버 연동|https://github.com/4m9d/sroom-be/pull/82 <br> - FastAPI에게 강의자료 생성 요청, 즉각 202 Response <br> - 자바 스케줄러를 통해 일정시간마다 Celery에서 처리된 task를 확인하고 강의자료를 받아와 저장 <br> - 이 방법을 통해 chatGPT를 사용하는 동안 스레드가 불필요하게 물려있는 현상을 해결|[손경식](https://github.com/Son-GyeongSik), [정두원](https://github.com/D-w-nJ)|
|2023-09-16||KMS 암호화 적용|https://github.com/4m9d/sroom-be/pull/98 <br> - AWS KMS(Key Management Service) 사용하여 DB url, username, pw, jwt secret, google client key 등을 암호화|[정두원](https://github.com/D-w-nJ)|
|2023-09-27|CI/CD|Github Action 사용 배포|https://github.com/4m9d/sroom-be/pull/104 <br> - Github Action, AWS ECS, ECR 사용한 CI/CD 구축 <br> - 프로덕션 환경과 같은 환경의 테스트 서버 구축|[정두원](https://github.com/D-w-nJ)|
|2023-10-30|리팩토링|객체의 협력과 책임을 고려한 프로그래밍|https://github.com/4m9d/sroom-be/pull/128 <br> - '오브젝트'책을 읽고 책임주도 설계를 적용시키고자 함 <br> - 객체에게 적절한 책임을 부과하고 협력하게끔 코드작성 <br> - 기존 Service 계층 코드길이가 200줄 이상되었던 문제점 해결|[정두원](https://github.com/D-w-nJ)|
|2023-11-03|성능개선|강의추천을 위한 rating방법 개선|https://github.com/4m9d/sroom-be/pull/134 <br> - 기존에 리뷰개수와 리뷰총합으로 계산, 정렬해오던 강의추천방식을 개선 <br> - rating칼럼을 생성하고, 일정주기로 accumulated_rating, review_count를 사용해 계산, 업데이트하여 추천될 때마다 rating을 인덱스로 하는 DB 리스트 업데이트를 최소화|[손경식](https://github.com/Son-GyeongSik)|
|2023-11-06|추가기능|ELK 구축|https://github.com/4m9d/sroom-be/pull/136<br> - ELK(Elasticsearch, Logstash, Kibana) 로그저장, 모니터링 툴 설치 <br> - 로그 내용을 Elasticsearch에서 사용 가능한 json 형태로 바꾸기 위해 Logstash에서 Grok 필터를 사용하여 해결|[정두원](https://github.com/D-w-nJ)|
|2023-01-26|리팩토링|JPA 사용|https://github.com/4m9d/sroom-be/pull/148 <br> - 기존 JdbcTemplate을 직접 사용하던 방식에서 JPA사용으로 리팩토링 <br> - 영속성 컨텍스트에 대한 쓰임 이해|[정두원](https://github.com/D-w-nJ), [손경식](https://github.com/Son-GyeongSik)|
|2023-02-27|성능개선|로컬 캐시 적용|https://github.com/4m9d/sroom-be/pull/152 <br> - 서로다른 사용자가 같은 유튜브 강의를 검색할 때, 해당 영상정보를 불러오기 위한 Youtube Data API 중복 호출하는 문제점 해결 <br> - tps 측정 가능한 nginder, 컴퓨터 리소스 모니터링 가능한 scouter사용하여 성능 확인 <br> - 1시간 이내 같은 키워드를 검색하는 경우 Latency 60% 감소|[정두원](https://github.com/D-w-nJ)|


<br>

## 개발 API


<br>

## 아키텍처
