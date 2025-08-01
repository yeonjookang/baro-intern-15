# Baro 인턴십 과제
본 프로젝트는 사용자 인증(회원가입, 로그인), 사용자 정보 조회 및 관리자 권한 부여/조회 기능을 제공하는 Spring Boot 기반의 RESTful API 서버입니다. 모든 데이터는 메모리 내에서 처리되며, Spring Security와 JWT를 활용하여 보안을 강화했습니다.
## 1. 주요 기능
- 사용자 인증: 회원가입 (/signup), 로그인 (/signin)
- 사용자 정보 조회: 현재 인증된 사용자 정보 조회 (/user)
- 관리자 기능: 사용자 역할 변경 (/admin/user/{userId}/role), 관리자 정보 조회 (/admin)
## 2. 실행 방법
- http://3.34.211.122:8080 에 접속하여 테스트합니다.
- 코드를 직접 클론받아 실행가능하지만, 보안상 jwt secret은 깃에 올려두지 않았습니다.
## 3. API 명세
본 프로젝트의 API 명세는 Swagger UI를 통해 제공됩니다.
- Swagger UI 주소: http://3.34.211.122:8080/swagger-ui.html <br>

Swagger UI에 접속하여 각 API 엔드포인트의 상세 설명, 요청/응답 구조, 상태 코드 등을 확인할 수 있습니다.