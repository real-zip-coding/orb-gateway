# Orb Gateway Microservices Project

## 프로젝트 개요

`Orb Gateway` 프로젝트는 마이크로서비스 아키텍처를 기반으로 구축된 시스템입니다. Spring Cloud를 활용하여 서비스 디스커버리, API 게이트웨이, 인증 서버 등의 핵심 컴포넌트를 제공합니다. 이 프로젝트의 목표는 안전하고 확장 가능한 마이크로서비스 환경을 구축을 목표로 하고있습니다.

## 프로젝트 구조

프로젝트는 multi-gradle로 여러 개의 독립적인 Spring Boot 애플리케이션(module)으로 구성되어 있으며, 각 모듈은 특정 기능을 담당합니다.

-   **`orb-auth-server`**:
    -   **역할**: 사용자 인증 및 인가(로그인, 회원가입), JWT(JSON Web Token) 발급 및 관리, JWKS(JSON Web Key Set) 엔드포인트 제공을 담당하는 인증 서버입니다.
    -   **기술**: Spring Security, JWT, Redis (토큰 블랙리스트 관리 등).

-   **`orb-eureka-server`**:
    -   **역할**: 서비스 디스커버리 서버입니다. 모든 마이크로서비스 인스턴스들이 여기에 자신을 등록하고, 다른 서비스의 위치를 조회할 수 있도록 합니다.
    -   **기술**: Spring Cloud Netflix Eureka.

-   **`orb-gateway-api`**:
    -   **역할**: API 게이트웨이 역할을 수행합니다. 클라이언트의 모든 요청을 받아 적절한 백엔드 서비스로 라우팅하며, JWT 토큰 검증, 요청 헤더 강화(사용자 정보 추가) 등 공통적인 횡단 관심사를 처리합니다.
    -   **기술**: Spring Cloud Gateway, Spring Security (OAuth2 Resource Server).

-   **`buildSrc`**:
    -   **역할**: Gradle 빌드 스크립트 및 플러그인을 중앙에서 관리하는 모듈입니다. 프로젝트 내 모든 서브 모듈의 빌드 설정을 일관성 있게 유지하는 데 사용됩니다.

## 주요 기술 스택

-   **언어**: Java
-   **프레임워크**: Spring Boot, Spring Cloud
-   **빌드 도구**: Gradle
-   **인증/인가**: JWT, Spring Security, OAuth2
-   **서비스 디스커버리**: Eureka
-   **API 게이트웨이**: Spring Cloud Gateway
-   **데이터베이스**: MySQL (orb-auth-server에서 사용자 정보 저장용)
-   **캐시**: Redis (orb-auth-server에서 토큰 블랙리스트 관리용)

## 시작하기

프로젝트를 로컬에서 실행하려면 다음 단계를 따르세요:

1.  **각 모듈 빌드**:
    ```bash
    ./gradlew clean build
    ```
2.  **서비스 실행 순서 (권장)**:
    1.  `orb-eureka-server` 실행 (가장 먼저 실행되어야 합니다.)
    2.  `orb-auth-server` 실행
    3.  `orb-gateway-api` 실행
    4.  (필요하다면) 다른 비즈니스 로직 마이크로서비스 실행

각 모듈은 IDE에서 직접 실행하거나, 빌드된 JAR 파일을 사용하여 실행할 수 있습니다.

```bash
# 예시: orb-eureka-server 실행
java -jar orb-eureka-server/build/libs/orb-eureka-server-0.0.1-SNAPSHOT.jar
```

## API 문서

각 서비스는 Swagger/OpenAPI를 통해 API 문서를 제공할 수 있습니다. 게이트웨이를 통해 접근하거나 각 서비스의 포트로 직접 접근하여 확인할 수 있습니다.

---
