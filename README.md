<div align="center">

![LOGO](https://github.com/user-attachments/assets/1c543861-c734-4300-99d3-0dcdeb0460e9)

### 웹 라이어 게임 프로젝트 🖍️
![프로젝트 기간](https://img.shields.io/badge/프로젝트_기간-2025.01.03~2025.02.03-fab2ac?style=flat&logo=&logoColor=white&color=2F3239)
<br/>
![사용 언어 수](https://img.shields.io/github/languages/count/SG5143/Lamong_us?label=%EC%82%AC%EC%9A%A9%20%EC%96%B8%EC%96%B4%20%EC%88%98&color=DEAA3A)
![커밋수](https://badgen.net/github/commits/SG5143/Lamong_us?label=%EC%BB%A4%EB%B0%8B%EC%88%98&color=DE3A86)
<br/>
![Java 17](https://img.shields.io/badge/Java_17-white)
![JSP/JSTL](https://img.shields.io/badge/JSP/JSTL-white)
![HTML5](https://img.shields.io/badge/HTML5-white?logo=html5&ogoColor=E34F26)
![CSS](https://img.shields.io/badge/CSS-white?logo=css3&logoColor=1572B6)
![JavaScript](https://img.shields.io/badge/JavaScript-white?logo=javascript&logoColor=F8DC75)
<br/>
![Apache_Tomcat](https://img.shields.io/badge/Apache_Tomcat-white?logo=apachetomcat&logoColor=F8DC75)
![Maven](https://img.shields.io/badge/Maven-white?logo=apachemaven&logoColor=C71A36)
![MySQL](https://img.shields.io/badge/MySQL-white?logo=mysql&logoColor=4479A1)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-white?logo=amazonrds&logoColor=527FFF)
![AWS_EC2](https://img.shields.io/badge/AWS_EC2-white?logo=amazonec2&logoColor=FF9900)
<br/>
![Eclipse_IDE](https://img.shields.io/badge/Eclipse_IDE-white?logo=eclipseide&logoColor=2C2255)
![Figma](https://img.shields.io/badge/Figma-white?logo=figma&logoColor=#F24E1E)
![Git](https://img.shields.io/badge/Git-white?logo=git&logoColor=F05032)
![GitHub](https://img.shields.io/badge/GitHub-white?logo=github&logoColor=181717)

<br/>

</div> 

## 📝 프로젝트 소개
채팅을 통한 웹 라이어 게임 <br/>
라몽어스는 심리전과 추리를 기반으로 한 라이어 게임을 온라인에서도 <br/>
여럿이서 소통하며 즐길 수 있는 서비스로 Websoket을 통해 구현했습니다.

- 주요 기능: 로그인/회원 관리, 게임방 생성, 입장, 퇴장, 채팅 게임 플레이
- 목표: 라이어 게임을 여럿이서 채팅으로 소통하며 즐길 수 있는 웹서비스 

<br/>

> ## 기술 스택
> - **언어**: Java 17
> - **빌드 도구**: Maven
> - **패키징**: WAR
> ## 사용 의존성
>
> | 이름                          | 사용 용도                              | 버전     | 주소                                                                 |
> |-------------------------------|----------------------------------------|----------|----------------------------------------------------------------------|
> | Jakarta Servlet API          | 서블릿 기반 웹 애플리케이션 개발       | 6.0.0    | [링크](https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/) |
> | Jakarta JSTL API             | JSP에서 태그 라이브러리 사용           | 3.0.1    | [링크](https://repo1.maven.org/maven2/jakarta/servlet/jsp/jstl/jakarta.servlet.jsp.jstl-api/3.0.1/) |
> | Glassfish JSTL Implementation| JSTL 구현체                           | 3.0.1    | [링크](https://repo1.maven.org/maven2/org/glassfish/web/jakarta.servlet.jsp.jstl/3.0.1/) |
> | Jakarta WebSocket API        | 실시간 양방향 통신 (웹소켓)            | 2.0.0    | [링크](https://repo1.maven.org/maven2/jakarta/websocket/jakarta.websocket-api/2.0.0/) |
> | MySQL Connector/J            | MySQL 데이터베이스 연결                | 8.0.33   | [링크](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/) |
> | jBCrypt                      | 비밀번호 해싱 및 암호화                | 0.4      | [링크](https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/)      |
> | org.json                     | JSON 데이터 생성 및 파싱               | 20240303 | [링크](https://repo1.maven.org/maven2/org/json/json/20240303/)       |


### 프로젝트 설계
|ERD|                                                Prototype                                                 |
|:---:|:--------------------------------------------------------------------------------------------------------:|
|<img src="https://github.com/user-attachments/assets/02bf7807-2b82-45db-9459-1b648b4aa79d" width="450"/>| <img src="https://github.com/user-attachments/assets/cb2fe935-1345-4ada-b976-7b31e5916823" width="450"/> |
|MediQuick 데이터베이스 ERD|                                            Lamongus 피그마 프로토타입                                            |

|요구사항 정의서|테이블 정의서|
|:---:|:---:|
|<img src="https://github.com/user-attachments/assets/7b08b619-3093-46fc-bbbf-d92cb1e21a15" width="450"/>|<img src="https://github.com/user-attachments/assets/220aa598-200a-42f8-bd14-39802ed3942f" width="450"/>|
|[Google Sheets 요구사항 정의서](https://docs.google.com/spreadsheets/d/1w8Qp6-so4S9i9Dc4c6akCD1ODvs9S9dy/edit?gid=1238616425#gid=1238616425)|[Google Sheets 테이블 정의서](https://docs.google.com/spreadsheets/d/1uRkU5UnoIgNP_O8g50IIlrVVk39gprzb/edit?gid=1413278482#gid=1413278482)|

|인터페이스 정의서|WBS|
|:---:|:---:|
|<img src="https://github.com/user-attachments/assets/d881e33a-c00f-4cb2-8cc5-436161c61cb5" width="450"/>|<img src="https://github.com/user-attachments/assets/3ee68190-40c6-4afe-8bf7-0899af922990" width="450"/>|
|[Google Sheets 인터페이스 정의서](https://docs.google.com/spreadsheets/d/1eApkbkirUXSYLtMBBslWZSKjCWFd1fET/edit?gid=2115368857#gid=2115368857)|[Google Sheets WBS](https://docs.google.com/spreadsheets/d/1Ww5kKaCK4KuqG7yo0GPBYI7uivgmTpqE/edit?gid=651336780#gid=651336780)|

<br/>

### 프로젝트 화면구성
|                                                   로그인                                                    |
|:--------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/245473ef-a8bb-43a7-aae3-0f6f2ae6cf96" width="450"/> |
|                      Lamong us는 라이어 웹 채팅 게임 서비스로<br/>로그인을 진행한 후 라이어 게임을 즐길 수 있습니다.                       |

|                                                   게임로비                                                   |
|:--------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/8f357548-80b4-4bd4-894a-7adb85035405" width="450"/> |
|                   로그인 후 게임 로비로 이동되고<br/>게임 로비에서는 게임방 생성 입장이 가능하며<br/>대기방에서는로비로 퇴장할 수 있습니다.                    |

|                                                   게임진행                                                   |
|:--------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/066b02bd-2259-466b-9750-70b89ed2983f" width="450"/> |
|          게임이 시작되면 역할과 순서가 배정되고<br/>번갈아가며 제시어를 설명하는 시간 30초가 주어집니다.<br/>이전에 입력한 제시어가 갱신되어 표기됩니다.           |

|                                                  라이어투표                                                   |
|:--------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/user-attachments/assets/c33fdc65-f3b6-4031-a180-3ac09e43368b" width="450"/> |
|  라운드가 모두 종료되면 자유채팅 시간이 주어집니다.<br/>이후 투표 모달로 넘어가 라이어를 투표할 수 있게 됩니다.<br/>모두 투표를 진행하거나 시간이 종료되면 결과를 출력합니다.  |

<br />

## 💁‍♂️ 프로젝트 팀원

|                                            로그인 및 회원관리                                             |                                             채팅 및 게임로직                                             |                                          로비 페이지, 게임 방 관리                                          |
|:-------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
| ![프로필1](https://github.com/user-attachments/assets/f850e168-a0f7-495f-a384-e48fd4d55e93?size=100) | ![프로필3](https://github.com/user-attachments/assets/b84e82b5-99d9-424f-a58b-05033a600bc6?size=100) | ![프로필4](https://github.com/user-attachments/assets/9b1df516-bfaa-4a46-9e51-5d34ca194dcd?size=100) |
|                                [허성원](https://github.com/sungwoni9)                                |                                 [이선구](https://github.com/SG5143)                                  |                                 [오세린](https://github.com/ohserin)                                 |

## ️🎬 발표 영상

[[작품발표] 이선구 팀 - 라몽어스 ](https://www.youtube.com/watch?v=bZAsnFbu2lQ)
