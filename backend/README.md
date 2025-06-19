# Voin Backend API

Voin(Value+Coin)은 20~30대가 자신의 장점(강점)을 인식하고 확장할 수 있게 돕는 자기이해 플랫폼입니다.

## 🚀 기술 스택

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Authentication**: JWT (추후 구현 예정)
- **Social Login**: Kakao API (추후 구현 예정)
- **Documentation**: Swagger/OpenAPI (추후 구현 예정)
- **Build Tool**: Gradle

## 📋 주요 기능

### 회원 관리

- 카카오 소셜 로그인 (추후 구현)
- 회원 정보 관리
- 친구 코드 시스템
- 닉네임 검색

### 카드(코인) 시스템

- 장점 카드 작성/수정/삭제
- 카드 공개/비공개 설정
- 카테고리별 분류
- 카드 좋아요 기능

### 친구 시스템 (추후 구현)

- 친구 요청/수락/거절
- 친구 코드로 친구 추가
- 친구들의 카드 피드

### 아카이브 시스템 (추후 구현)

- 내 카드 아카이브
- 즐겨찾기 기능
- 카테고리별 정리

## 🗄️ 데이터베이스 설계

### 주요 엔티티

- **Member**: 사용자 정보
- **Card**: 장점 카드
- **Category**: 장점 카테고리
- **Value**: 카드의 핵심 가치
- **Friend**: 친구 관계
- **Archive**: 카드 아카이브
- **Form**: 작성 폼 템플릿

## 🛠️ 설치 및 실행

### 사전 요구사항

- Java 17
- PostgreSQL 12+
- Gradle 8.0+

### 데이터베이스 설정

```sql
CREATE DATABASE voin_db;
CREATE USER voin_user WITH PASSWORD 'voin_password';
GRANT ALL PRIVILEGES ON DATABASE voin_db TO voin_user;
```

### 환경 변수 설정

```bash
export DB_USERNAME=voin_user
export DB_PASSWORD=voin_password
export JWT_SECRET=your-jwt-secret-key
export KAKAO_CLIENT_ID=your-kakao-client-id
export KAKAO_CLIENT_SECRET=your-kakao-client-secret
```

### 실행

```bash
# 프로젝트 클론
git clone <repository-url>
cd voin/backend

# 빌드 및 실행
./gradlew bootRun
```

## 📚 API 문서

### 회원 관리 API

- `GET /api/members/{id}` - 회원 정보 조회
- `GET /api/members/friend-code/{friendCode}` - 친구 코드로 회원 조회
- `GET /api/members/search?keyword={keyword}` - 닉네임 검색
- `PUT /api/members/{id}` - 회원 정보 수정
- `POST /api/members/{id}/friend-code/regenerate` - 친구 코드 재생성
- `DELETE /api/members/{id}` - 회원 탈퇴

### 카드 관리 API

- `GET /api/cards/{id}` - 카드 상세 조회
- `GET /api/cards/member/{memberId}` - 회원의 카드 목록
- `GET /api/cards/public` - 공개 카드 목록
- `GET /api/cards/category/{categoryId}` - 카테고리별 카드 조회
- `GET /api/cards/search?keyword={keyword}` - 카드 내용 검색
- `POST /api/cards` - 카드 생성
- `PUT /api/cards/{id}` - 카드 수정
- `DELETE /api/cards/{id}` - 카드 삭제
- `POST /api/cards/{id}/toggle-visibility` - 공개/비공개 전환
- `POST /api/cards/{id}/like` - 카드 좋아요

## 🏗️ 프로젝트 구조

```
src/main/java/com/voin/
├── config/          # 설정 클래스
├── constant/        # 상수 및 Enum
├── controller/      # REST 컨트롤러
├── dto/            # 데이터 전송 객체
│   ├── request/    # 요청 DTO
│   └── response/   # 응답 DTO
├── entity/         # JPA 엔티티
├── exception/      # 예외 클래스
├── repository/     # 데이터 접근 계층
├── service/        # 비즈니스 로직
└── util/           # 유틸리티 클래스
```

## 🔧 개발 상태

### ✅ 완료된 기능

- 기본 엔티티 설계 및 구현
- 회원 관리 API
- 카드 관리 API
- 카테고리 시스템
- 기본 데이터 초기화

### 🚧 진행 중인 기능

- JWT 인증 시스템
- 카카오 소셜 로그인
- Swagger API 문서화

### 📋 예정된 기능

- 친구 시스템 구현
- 아카이브 시스템 구현
- 폼 시스템 구현
- 알림 시스템
- 테스트 코드 작성

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이센스

이 프로젝트는 MIT 라이센스 하에 있습니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

프로젝트 관련 문의: [이메일 주소]

프로젝트 링크: [GitHub 저장소 URL]
