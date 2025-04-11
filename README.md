# 무신사 과제 - 최저가 상품 조회 API 구현

## 프로젝트 개요
이 프로젝트는 무신사 백엔드 개발자 과제에 대한 구현입니다. 상품, 브랜드, 카테고리 데이터를 관리하고 최저가 상품을 조회하는 API를 제공합니다.

## 기술 스택
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.3.10
- **데이터베이스**: H2 (인메모리 DB)
- **캐싱**: Redis
- **ORM**: JPA/Hibernate
- **데이터베이스 마이그레이션**: Flyway
- **빌드 도구**: Gradle
- **컨테이너화**: Docker, Docker Compose
- **기타 라이브러리**: 
  - Lombok
  - Json-Patch
  - Spring Data Redis
  - Spring Boot Cache

## 구현 기능
과제 요구사항에 대한 구현 상태는 다음과 같습니다:

### 1. 기본 데이터 모델 설계
- [x] 상품(Product) 엔티티 구현
- [x] 브랜드(Brand) 엔티티 구현
- [x] 카테고리(Category) 엔티티 구현
- [x] 엔티티 간 관계 매핑 (일대다 관계 등)

### 2. REST API 구현
- [x] 각 카테고리별 최저가격 브랜드와 상품 가격 조회 API
- [x] 브랜드별로 모든 카테고리의 최저가격 상품 조회 API
- [x] 카테고리별 최저가, 최고가 상품 조회 API
- [x] 상품 생성, 수정, 삭제 API
- [x] 브랜드 생성, 수정, 삭제 API
- [x] 상품 가격 변경 API

### 3. 성능 최적화
- [x] 캐싱 적용 (Redis)
- [x] 쿼리 최적화
- [x] 페이지네이션 구현 (대용량 데이터 처리)
- [x] 데이터베이스 인덱싱

### 4. 동시성 처리
- [x] 비관적 락(Pessimistic Lock) 구현
- [x] 트랜잭션 관리

## 주요 아키텍처 설계
프로젝트는 다음과 같은 계층 구조로 설계되었습니다:

1. **Controller 계층**: API 엔드포인트 정의 및 요청/응답 처리
2. **Application 계층**: 비즈니스 로직 처리
3. **Domain 계층**: 핵심 비즈니스 로직 및 엔티티 정의
4. **Repository 계층**: 데이터 액세스 처리

## 최적화 및 성능 개선 포인트
- **캐싱**: Redis를 사용한 결과 캐싱으로 반복 요청에 대한 DB 부하 감소
- **쿼리 최적화**: 
  - 조인 패치(Join Fetch)를 통한 N+1 문제 해결
  - 복잡한 집계 쿼리의 효율적 구현
- **데이터베이스 인덱스**: 카테고리명, 브랜드명 등 주요 필드에 인덱스 적용
- **페이지네이션**: 대용량 데이터 조회 시 페이지네이션 적용으로 메모리 사용량 최적화

## API 설명

### 1. 카테고리별 최저가 상품 조회
- **엔드포인트**: GET `/api/v1/categories/lowest-price-by-category`
- **응답 예시**: 각 카테고리별 최저가격 브랜드와 상품 가격 정보

### 2. 브랜드별 카테고리 최저가 상품 조회
- **엔드포인트**: GET `/api/v1/brands/lowest-price`
- **응답 예시**: 모든 카테고리 상품의 가격 합계가 가장 저렴한 브랜드와 상품 가격 정보

### 3. 카테고리 최저가/최고가 상품 조회
- **엔드포인트**: GET `/api/v1/categories/{categoryName}/price-summary`
- **응답 예시**: 특정 카테고리의 최저가 및 최고가 상품 정보

### 4. 상품 가격 변경
- **엔드포인트**: PATCH `/api/v1/products/{productId}`
- **요청 예시**: JSON Patch 형식의 가격 변경 정보
- **응답 예시**: 변경된 상품 정보

## 실행 방법

### 로컬 환경에서 실행
1. 프로젝트 클론: `git clone [레포지토리 URL]`
2. 프로젝트 빌드: `./gradlew build`
3. Docker Compose로 실행:
   ```bash
   docker compose up --build -d
   ```
    - Spring Boot 애플리케이션과 Redis가 자동으로 실행됩니다.
    - 애플리케이션은 8080 포트에서 접근 가능합니다.
    - Redis는 6379 포트에서 실행됩니다.

4. 서비스 중지:
   ```bash
   docker compose down
   ```
5. 테스트 실행: `./gradlew test` (JUnit5 기반)

## 데이터베이스 스키마
- **categories**: 카테고리 정보 (id, name)
- **brands**: 브랜드 정보 (id, name)
- **products**: 상품 정보 (id, name, price, category_id, brand_id)

Flyway를 사용하여 애플리케이션 시작 시 자동으로 스키마가 생성되고 초기 데이터가 로드됩니다.
