# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- `./gradlew build` - Build the project
- `./gradlew bootRun` - Run the Spring Boot application
- `./gradlew test` - Run tests

### Development Profile
- Use `application-dev.yml` profile for local development
- Default server runs on `http://localhost:8080`
- H2 database for testing, MySQL for production

### API Documentation
- Swagger UI available at `/swagger-ui.html`
- API docs available at `/api-docs`

## Project Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: JPA with MySQL (production), H2 (testing)  
- **Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Gradle

### Package Structure
The project follows a hybrid approach using both DDD and layered architecture patterns:

- **Common Layer** (`com.kkulddip.common`)
  - `config/` - Application configuration (Swagger, etc.)
  - `exception/` - Global exception handling with `GlobalExceptionHandler`
  - `response/` - Standardized API response objects

### Error Handling System
- Uses centralized exception handling via `GlobalExceptionHandler`
- Custom `BusinessException` with domain-specific `ErrorCode` enums
- Standardized error responses with `ErrorResponse<T>` and `ValidationError`
- Error codes organized by domain (COMMON, USER, AUTH, ORDER)

### Response Standards
- Success responses use `ApiResponse<T>` with `success`, `status`, and `body` fields
- Error responses use `ErrorResponse<T>` with error code, message, and optional data
- All responses include HTTP status codes

## Code Conventions

### Naming Conventions
| 항목 | 명사/동사 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- | --- |
| 📦 클래스명 | **명사** | `PascalCase` | `User`, `OrderService`, `UserController` | 실체, 역할을 나타내는 이름 |
| 🔧 메서드명 | **동사** (또는 동사+명사) | `camelCase` | `getUser()`, `updateOrder()` | 동작이나 기능 수행 |
| 📄 변수명 | **명사** | `camelCase` | `user`, `productList` | 데이터를 담는 객체 이름 |
| ✅ Boolean 변수 | **동사**+의미 | `is`, `has`, `can` | `isActive`, `hasPermission`, `canEdit` | 상태, 가능 여부 표현 |
| 🧱 패키지명 | **명사 (복수 지양)** | `lowercase` | `user`, `order`, `payment` | 도메인이나 기능 단위 |
| 🔠 상수명 | **명사** | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`, `DEFAULT_ROLE` | 변경되지 않는 값 |
| 🌐 API Endpoints | - | `kebab-case` | `/api/user-orders`, `/auth/login` | REST API 엔드포인트 |
| 🗄️ DB 컬럼명 | **명사** | `snake_case` | `user_id`, `created_at` | 데이터베이스 컬럼 |

### File Structure and Formatting
- **Encoding**: UTF-8 방식 사용
- **Indentation**: 스페이스바 4개 단위 (tabs 금지)
- **Whitespace**: 뒤에 있는 whitespace 제거
- **Brace Style**: K&R 스타일 (Kernighan and Ritchie style)
  - 여는 괄호 앞에는 줄 바꿈이 없음
  - 여는 괄호 다음에 줄 바꿈
  - 닫는 괄호 전에 줄 바꿈
  - 빈 블럭은 `{}` 또는 `{\n}` 형태로 간결하게 표현 가능

### Spacing Rules
#### 수직 공백 (줄 바꿈)
- 가독성을 위한 빈 줄 사용 가능
- 여러 줄의 연속적인 빈 줄은 권장하지 않음
- **Enum 예외**: 메소드와 documentation이 없는 enum은 `private enum Suit { CLUBS, HEARTS, SPADES, DIAMONDS }` 형태 가능

#### 수평 공백
1. `if`, `for`, `catch` 등 예약어와 그 뒤 괄호(`(`) 사이
2. `else`, `catch` 등 예약어와 그 앞 중괄호(`}`) 사이
3. 중괄호(`{`) 앞 (배열 초기화 예외)
4. 모든 이항/삼항 연산자 양쪽
5. 쉼표(`,`), 콜론(`:`), 세미콜론(`;`), 괄호 닫힘(`)`) 뒤
6. 주석 시작 `//` 앞과 뒤
7. 변수 선언에서 타입과 변수명 사이
8. 타입 애노테이션과 `[]`, `...` 사이

### Class Content Order
1. 상수 선언
2. 필드 선언
3. 생성자
4. `@PostConstruct` 등 초기화 메서드
5. public 메서드
6. private 메서드

### Annotations Order
| 순서 | 범주 | 예시 어노테이션 |
| --- | --- | --- |
| 1 | **로깅 및 코드 생성용** | `@Slf4j`, `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@NoArgsConstructor` |
| 2 | **스프링 컴포넌트 정의** | `@RestController`, `@Controller`, `@Service`, `@Repository`, `@Component` |
| 3 | **요청 매핑 관련** | `@RequestMapping`, `@GetMapping`, `@PostMapping` |
| 4 | **문서화 / API 명세용** | `@Tag`, `@Operation`, `@Api`, `@ApiResponses` |
| 5 | (옵션) **AOP, 트랜잭션, 보안 등 부가기능** | `@Transactional`, `@PreAuthorize` 등 |

### Variable Declaration
- **선언당 하나의 변수** (One variable per declaration)
- **필요할 때 선언** (Declared when needed) - 처음 사용되는 지점 가까이에서 선언
- **즉시 초기화** - 선언과 동시에 값 할당 또는 선언 직후 초기화
- **For문 예외**: for문 헤더에서는 여러 변수 함께 선언 가능

### Object Creation and DTOs
- **사용자 정의 클래스**: 최대한 Lombok의 `@Builder` 패턴 이용
- **Builder 패턴 스타일**:
  ```java
  someMethod(
      SomeDto.builder()
          .field1(value1)
          .field2(value2)
          .build()
  );
  ```

### DTO Conventions
- **메서드 없는 경우**:
  ```java
  public record UserDto(
      String name,
      String email,
      int age,
      String address
  ) {}
  ```
- **메서드 있는 경우**:
  ```java
  public record UserDto(
      String name,
      String email
  ) {
      public String displayName() {
          return name + " <" + email + ">";
      }
  }
  ```

### Package Organization
#### DDD 기반 패키지 구조
각 도메인별로 `presentation`, `application`, `domain`, `infrastructure`로 구성:
- **domain 계층**: `model/`, `repository/`, `service/`, `event/`
  - **model**: `entity/`, `vo/`, `enums/`, `aggregate/`
- **presentation 계층**: `rest/`, `event/`, `scheduled/` (상황에 따라)
  - **dto**: `request/`, `response/`
- **application 계층**: `service/`, `facade/`, `mapper/`
- **infrastructure 계층**: `persistence/`, `external/`
  - **persistence**: `jpa/`, `redis/` 등 저장소별 분리

#### 레이어드 아키텍처 기반 패키지 구조
각 도메인별로 `controller/`, `dto/`, `service/`, `entity/`, `repository/`로 구성:
- **dto**: `request/`, `response/`
- **entity**: `enums/` (enum class 저장)

## Import Rules
- **와일드카드 금지**: `import jakarta.persistence.*;` 같은 방법 금지
- **Static 임포트**: 테스트 코드에서만 허용
- **줄바꿈 금지**: 열 제한이 적용되지 않음
- **필요한 것만**: 사용하는 클래스만 개별적으로 임포트