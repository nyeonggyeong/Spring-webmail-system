# 📧 Spring Webmail System

> Apache James 메일 서버와 연동하는 **Spring Boot 기반 웹메일 시스템**  
> 객체지향설계(OOD) 프로젝트 — 교정 · 예방 · 완전화 유지보수 적용

---

## 🛠 Tech Stack

**Backend**  
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring MVC](https://img.shields.io/badge/Spring_MVC-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

**Mail Protocol**  
![POP3](https://img.shields.io/badge/POP3-D22128?style=for-the-badge&logo=apache&logoColor=white)
![SMTP](https://img.shields.io/badge/SMTP-D22128?style=for-the-badge&logo=apache&logoColor=white)
![Apache James](https://img.shields.io/badge/Apache_James-D22128?style=for-the-badge&logo=apache&logoColor=white)

**Database**  
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![JdbcTemplate](https://img.shields.io/badge/JdbcTemplate-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

**View**  
![JSP](https://img.shields.io/badge/JSP-007396?style=for-the-badge&logo=openjdk&logoColor=white)

**Test**  
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-25A162?style=for-the-badge&logo=junit5&logoColor=white)

**정적 분석**  
![PMD](https://img.shields.io/badge/PMD-005571?style=for-the-badge&logo=scrutinizer-ci&logoColor=white)


---

## 📁 프로젝트 구조

```
src/main/java/deu/cse/spring_webmail/
├── control/
│   ├── SystemController.java     # 로그인, 메인 메뉴
│   ├── ReadController.java       # 메일 읽기, 휴지통, 보낸편지함
│   ├── WriteController.java      # 메일 작성, 자동 임시저장
│   └── AddressController.java    # 주소록 CRUD
├── model/
│   ├── Pop3Agent.java            # POP3 메일 수신, 검색, 정렬, 페이징
│   ├── SmtpAgent.java            # SMTP 메일 발송
│   ├── MessageFormatter.java     # 메일 HTML 렌더링
│   ├── MessageParser.java        # 메일 파싱
│   ├── EmailTrashAgent.java      # 휴지통 DB 처리
│   ├── SentMailAgent.java        # 보낸편지함 DB 처리
│   ├── AddressBookAgent.java     # 주소록 DB 처리
│   ├── UserAdminAgent.java       # 사용자 관리 (James 서버)
│   └── *Dto.java                 # 데이터 전달 객체
└── SpringWebmailApplication.java
```

---

## ✨ 주요 기능

### 📥 메일 수신 (POP3)
- 메일 목록 조회 (페이징 처리)
- 제목 / 발신자 기준 검색
- 최신순 정렬 (In-Memory Sort)
- 메일 상세 보기 및 첨부파일 다운로드

### 📤 메일 발송 (SMTP)
- 메일 작성 및 발송 (첨부파일 포함)
- 메일 작성 중 AJAX 자동 임시저장 (`ON DUPLICATE KEY UPDATE`)

### 🗑 휴지통
- 메일 삭제 시 DB(`email_trash`)에 자동 백업
- 휴지통 메일 복구 (SMTP 재전송)
- 영구 삭제 / 전체 비우기

### 📨 보낸 편지함
- 메일 발송 성공 시 DB(`sent_mail`)에 자동 저장
- 발송 이력 조회 및 삭제

### 📒 주소록
- 주소록 CRUD (`address_book` 테이블)
- 메일 작성 화면에서 팝업으로 수신자 자동 입력

### 👤 사용자 관리 (관리자)
- Apache James 서버 telnet 연동
- 사용자 추가 / 삭제

---

## 🔧 유지보수 내역

### ✅ 교정 유지보수 (Corrective)
PMD 정적 분석 도구를 활용하여 기존 코드의 결함을 탐지하고 수정하였다.

| 항목 | 내용 |
|------|------|
| `ReturnFromFinallyBlock` | `finally` 블록 내 `return` 문 제거 → 표준 예외 흐름 복원 |
| `EmptyCatchBlock` | 빈 `catch` 블록에 `log.error()` 추가 → 예외 추적성 확보 |
| 기본 생성자 제거 | `Pop3Agent()` 기본 생성자 제거 → 필수 인자 강제로 객체 무결성 확보 |

### ✅ 예방 유지보수 (Preventive)
코드 스멜을 제거하고 유지보수성을 향상시키기 위한 리팩토링을 수행하였다.

| 항목 | 내용 |
|------|------|
| Lombok 도입 | `@Getter/@Setter/@Slf4j/@RequiredArgsConstructor` 적용 → 보일러플레이트 제거 |
| 책임 분리 | HTML 생성 로직을 `MessageFormatter`로 분리 → SRP 준수 |
| 데드코드 제거 | 주석 처리된 코드, 미사용 import/변수 정리 |
| 하위 호환성 유지 | `getMessageList()` 오버로딩으로 기존 코드와 호환성 보장 |

### ✅ 완전화 유지보수 (Perfective)
사용자 편의성을 높이기 위한 신규 기능을 추가하였다.

| 기능 | 내용 |
|------|------|
| 메일 목록 고도화 | 페이징, 제목/발신자 검색, 최신순 정렬 |
| 휴지통 | 삭제→이동, 복구, 영구삭제 |
| 보낸 편지함 | 발송 이력 자동 저장, 조회, 삭제 |
| 자동 임시저장 | AJAX 비동기 저장, `ON DUPLICATE KEY UPDATE` 적용 |
| 주소록 | DB 설계부터 CRUD 전체 구현, 메일 작성 팝업 연동 |

---

## 🧪 테스트 자동화

JUnit5 + Mockito를 활용한 단위 테스트 작성 (총 9개 테스트 파일)

| 테스트 파일 | 대상 | 기법 |
|------------|------|------|
| `AddressBookAgentTest` | 주소록 CRUD | 동등 분할, 경계값 분석 |
| `Pop3AgentSearchTest` | 메일 검색 필터링 | 동등 분할 |
| `Pop3AgentSortTest` | 날짜 정렬 | 경계값 분석 |
| `EmailTrashAgentTest` | 휴지통 저장/조회/삭제 | 동등 분할, 보안 검증 |
| `SentMailAgentTest` | 보낸편지함 저장/조회/삭제 | 동등 분할, 보안 검증 |
| `WriteControllerTest` | 자동 임시저장 | 동등 분할 |
| `DatabaseConnectionTest` | DB 연결 상태 | 통합 테스트 |

---

## ⚙️ 환경 설정

### 1. Apache James 메일 서버 설치
```bash
# James 서버 실행 (Windows)
james-3.x.x/bin/run.bat

# 메일 계정 생성 (telnet)
telnet localhost 4555
adduser [userid] [password]
```

### 2. MariaDB 설정
```sql
CREATE DATABASE webmail CHARACTER SET utf8mb4;
CREATE TABLE email_trash (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userid VARCHAR(50),
    sender VARCHAR(100),
    subject VARCHAR(200),
    body TEXT,
    deleted_date DATETIME DEFAULT NOW()
);
CREATE TABLE sent_mail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userid VARCHAR(50),
    receiver VARCHAR(100),
    subject VARCHAR(200),
    body TEXT,
    sent_date DATETIME DEFAULT NOW()
);
CREATE TABLE address_book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userid VARCHAR(50),
    name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20)
);
CREATE TABLE draft_mail (
    userid VARCHAR(50) PRIMARY KEY,
    receiver VARCHAR(100),
    cc VARCHAR(100),
    subject VARCHAR(200),
    body TEXT,
    save_time DATETIME DEFAULT NOW()
);
```

### 3. application.properties 설정
```properties
server.port=8888
server.servlet.context-path=/webmail

spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/webmail?characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. 실행
```bash
# Maven
mvn spring-boot:run

# 접속
http://localhost:8888/webmail
```

---

## 🔑 포트 설정

| 서비스 | 포트 |
|--------|------|
| Spring Boot | 8888 |
| SMTP | 25 |
| POP3 | 110 |
| MariaDB | 3306 |
| James 관리 | 4555 |

---

## 📐 설계 원칙 적용

| 원칙/패턴 | 적용 위치 |
|----------|----------|
| **GRASP - Controller** | `SystemController`, `ReadController`, `WriteController`, `AddressController` |
| **GRASP - Information Expert** | `EmailTrashAgent`, `SentMailAgent`, `AddressBookAgent` |
| **SOLID - SRP** | `MessageFormatter` 분리, 각 Agent 클래스 단일 책임 |
| **SOLID - DIP** | `JdbcTemplate` 생성자 주입 (`@Autowired`) |
| **SOLID - OCP** | `getMessageList()` 오버로딩으로 기존 코드 수정 없이 기능 확장 |
