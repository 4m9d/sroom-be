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

### API 버전관리
- [SwaggerHub](https://app.swaggerhub.com/apis/sroom/api.sroom.com/2.0.0)
- yaml 파일로 작성, 현재 2.0.0 ver

<br>

### 스크럼 - 2주 단위 스프린트
- Jira 칸반보드 사용 

- Confluence 사용한 스프린트 플래닝, 데일리 스크럼, KPT 회고, 백로그 작성

<br>

### 이슈 및 PR 생성
- [이슈 템플릿](https://github.com/4m9d/sroom-be/blob/main/.github/ISSUE_TEMPLATE.md)에 따라 개요, 이유, 세부사항 작성

- [PR 템플릿](https://github.com/4m9d/sroom-be/blob/main/.github/PULL_REQUEST_TEMPLATE.md)에 따라 Motivation, Key changes, To reviewers 작성

<br>

### MockAPI 사용
- Postman MockAPI 사용

- 프론트엔드 팀원에게 원활한 개발환경을 제공

<br>

### 이슈관리
- Slack의 스레드 생성해 논의

- 필요하다면 백로그 추가

<br>

### 테스트 서버 구축
- 프로덕션 환경과 같은 환경으로 테스트 서버 구축

- Pull Request 할 시 테스트서버 반영하여 디버깅 


<br>

### 브랜치 전략
- Github-flow 방식 채택

- 생성된 지라이슈 티켓으로 브랜치 생성, 개발 완료시 Main으로 PR, Merge

