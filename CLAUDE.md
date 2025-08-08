# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- `./gradlew build` - Build the project and run tests
- `./gradlew bootRun` - Run the Spring Boot application
- `./gradlew clean build` - Clean build (used in CI/CD)
- `./gradlew test` - Run all unit tests
- `./gradlew bootJar` - Create executable JAR

### Development Environment
- Java 21 with Spring Boot 3.5.3
- Gradle build system with wrapper
- Uses MySQL database with Redis for caching
- H2 database for development/testing
- OpenAPI/Swagger documentation at `/swagger-ui.html`

## Architecture Overview

This is a microservices-oriented Spring Boot application implementing **Domain-Driven Design (DDD)** with some domains using traditional layered architecture.

### Core Domains
- **Order Domain** (DDD) - Complete order management with aggregates, value objects, and domain services
- **Payment Domain** (DDD) - Payment processing with Toss Payments integration  
- **User Domain** (Layered) - User management with Customer/Owner entities

### Domain Structure (DDD)
Each DDD domain (order, payment) follows this structure:
```
domain/
├── presentation/
│   ├── rest/         # REST controllers and DTOs
│   └── event/        # Event listeners
├── application/
│   ├── facade/       # Application facades
│   ├── service/      # Application services
│   └── mapper/       # Domain-DTO mappers
├── domain/
│   ├── model/
│   │   ├── aggregate/  # Domain aggregates (e.g., Order)
│   │   ├── entity/     # Domain entities
│   │   ├── vo/         # Value objects (OrderId, Money)
│   │   └── enums/      # Domain enumerations
│   ├── repository/     # Repository interfaces
│   └── service/        # Domain services
└── infrastructure/
    ├── persistence/    # JPA entities and repository implementations
    │   ├── jpa/
    │   └── redis/
    └── external/       # External service integrations
```

### Key Architectural Components
- **Aggregates**: Order is the main aggregate root with OrderItem entities
- **Value Objects**: Money, OrderId, CustomerId for type safety
- **Domain Events**: OrderCreatedEvent, PaymentResultEvent for loose coupling
- **Command Pattern**: AddOrderItemCommand for domain operations
- **Repository Pattern**: Clean separation between domain and persistence

### Technology Stack
- **Framework**: Spring Boot 3.5.3, Spring Security, Spring Data JPA
- **Authentication**: OAuth2 (Google) with JWT tokens
- **Database**: MySQL with Redis for caching and event publishing
- **Payment**: Toss Payments API integration
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Spring Boot Test

### Key Business Rules
- Order state transitions: CREATED → PAYMENT_PENDING → PAID → AWAITING_CONFIRMATION → CONFIRMED
- Orders can only be modified in CREATED state
- Payment processing is asynchronous with event-driven confirmation
- OAuth2 supports separate customer and owner login flows

## Code Conventions

### 네이밍 컨벤션
- API Endpoint: kebab-case
- Class: PascalCase
- 메서드/변수명: camelCase
- static 상수: UPPER_SNAKE_CASE
- DB 컬럼명: snake_case

| 항목 | 명사/동사 | 형식 | 예시 | 설명 |
| --- | --- | --- | --- | --- |
| 📦 클래스명 | **명사** | `PascalCase` | `User`, `OrderService`, `UserController` | 실체, 역할을 나타내는 이름 |
| 🔧 메서드명 | **동사** (또는 동사+명사) | `camelCase` | `getUser()`, `updateOrder()` | 동작이나 기능 수행 |
| 📄 변수명 | **명사** | `camelCase` | `user`, `productList` | 데이터를 담는 객체 이름 |
| ✅ Boolean 변수 | **동사**+의미 | `is`, `has`, `can` | `isActive`, `hasPermission`, `canEdit` | 상태, 가능 여부 표현 |
| 🧱 패키지명 | **명사 (복수 지양)** | `lowercase` | `user`, `order`, `payment` | 도메인이나 기능 단위 |
| 🔠 상수명 | **명사** | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`, `DEFAULT_ROLE` | 변경되지 않는 값 |

### 파일 인코딩
- UTF-8 방식 사용

### 인덴테이션
- 인덴테이션은 스페이스바 사용 (not tabs)
- 들여쓰기는 4개의 빈 칸 단위
  - Continuation indentation도 4칸으로 통일한다.
- 뒤에 있는 whitespaces들은 제거

### 중괄호 스타일
- 괄호는 if, else, for, do, while 구문에 쓰이는데 몸체가 없거나 한 줄의 구문에도 괄호가 쓰인다.
- K&R 스타일(Kernighan and Ritchie style)을 따른다.
  - 여는 괄호 앞에는 줄 바꿈이 없음
  - 여는 괄호 다음에 줄 바꿈
  - 닫는 괄호 전에 줄 바꿈
  - 닫는 괄호 다음에 줄 바꿈, 그런데 이것은 오직 구문이 끝나거나 메소드, 생성자, 클래스가 끝났을 때 적용된다. 예를 들어, else나 콤마 뒤에 나오는 부분은 줄 바꿈을 하지 않는다.
- 빈 블럭은 간결하게 표현 가능
  - { } 괄호 안에 문자가 없거나 줄바꿈이라면 열자마자 끝날 수 있다. 하지만 멀티 블럭 구문에서는 할 수 없다.
  
  ```java
  // 허용
  void doNothing() {}
  
  // 허용
  void doNothing() {
  }
  
  // 허용되지 않음: 멀티 블럭 구문에서는 간결한 빈 블럭을 사용할 수 없음
  try {
      doSomething();
  } catch (Exception e) {}
  ```

### 공백

- 수직 공백 (줄 바꿈)
  - 가독성을 높이기 위한 경우, 한 줄의 빈 줄을 아무 곳에나 사용할 수 있습니다. 예를 들어, 여러 문장 사이에 빈 줄을 넣어 코드를 논리적인 부분으로 나눌 수 있습니다.
  - 여러 줄의 연속적인 빈 줄은 허용되지만, 절대 필수는 아니며 권장되지도 않습니다.
  - 예외 — Enum class
      
    메소드와 documentation이 없는 enum 클래스는 배열 초기화와 같은 포맷으로 작성될 수 있다.
    
    ```java
    private enum Suit { CLUBS, HEARTS, SPADES, DIAMONDS }
    ```
        
- 수평 공백
    
  프로그래밍 언어나 다른 스타일 규칙에서 요구되는 경우를 제외하고, 리터럴, 주석, Javadoc을 제외하면, ASCII 공백 문자 하나는 다음의 경우에만 사용됩니다:
  
  1. `if`, `for`, `catch` 등 예약어와 그 뒤에 오는 괄호(`(`) 사이
    예: `if (condition)`
      
  2. `else`, `catch` 등 예약어와 그 앞에 오는 중괄호(`}`) 사이
    예: `} else {`
      
  3. 중괄호(`{`) 앞
    - 예외:
      - 애노테이션에 배열을 사용할 때: `@SomeAnnotation({a, b})` → 공백 없음
      - 중첩 배열 초기화: `String[][] x = {{"foo"}};` → `{{` 사이에 공백 불필요
  4. 모든 이항 연산자 및 삼항 연산자 양쪽
    - 다음과 같은 "연산자와 유사한" 기호도 포함됩니다:
      - & 연산자 (타입 경계): `<T extends Foo & Bar>`
      - | 연산자 (다중 예외 처리): `catch (FooException | BarException e)`
      - 향상된 for문의 콜론 `:`: `for (Item item : items)`
      - 람다 표현식의 화살표 `>`: `(String s) -> s.length()`
      - `switch`의 화살표: `case "FOO" -> bar();`
    - 제외:
      - 메서드 참조의 `::` → `Object::toString`
      - 점(.) 구분자 → `object.toString()`
  5. 쉼표(,), 콜론(:), 세미콜론(;), 괄호 닫힘(`)`) 뒤
    예: `return (String) object;`
      
  6. 주석을 시작하는 `//` 앞과 뒤
    - 예: `int a = 0; // 설명`
    - `//` 앞에는 공백이 있어야 하며, 뒤에는 한 개 이상의 공백이 허용됩니다.
  7. 변수 선언에서 타입과 변수명 사이
    - 예: `List<String> list`
  8. (선택) 배열 초기화 블록의 중괄호 `{}` 안쪽
    - `new int[] {5, 6}` 및 `new int[] { 5, 6 }` 모두 허용이지만, 전자를 권장
  9. 타입 애노테이션과 `[]`, `...` 사이
    - 예: `@Nullable String[] arr`

### 임포트 구문
- 와일드 카드(`*`) 임포트는 금지. 예를 들어, import jakarta.persistence.*; 와 같은 방법은 금지되어 있다. 필요한 것만 사용하도록 한다.
- static 임포트는 테스트 코드 작성에서만 허용
- 줄바꿈 금지(열 제한이 적용되지 않음)

### 클래스 내용 순서
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

### 변수 선언
- 선언당 하나의 변수 (One variable per declaration)
  - 모든 변수 선언(필드 또는 지역 변수)은 하나의 변수만 선언해야 합니다.

- 예외: for문 헤더에서는 여러 변수를 함께 선언해도 괜찮습니다
        
- 필요할 때 선언 (Declared when needed)
  - 지역 변수(local variable)는 해당 블록이나 유사한 블록 구조의 시작 부분에서 습관적으로 선언하지 않습니다.
  - 대신, 변수는 처음 사용되는 지점 가까이에서 선언하여, 그 변수의 유효 범위를 최소화합니다.
  - 지역 변수는 일반적으로 선언과 동시에 값을 할당(초기화)하거나, 선언 직후 즉시 초기화됩니다.

### 객체 생성 방식
- DTO와 같은 사용자가 만든 클래스들에 대해서는 최대한 lombok의 빌더 패턴을 이용한다.
        
### 롬복
- Lombok의 @Builder 패턴을 사용하여 메서드 인자로 객체를 인라인으로 생성할 때의 코드 스타일 규칙은 다음과 같다.

  1. 메서드 호출의 여는 괄호 ( 다음 줄에서 builder()를 시작한다.
  2. 빌더 체이닝은 각 단계마다 한 줄씩 작성하고 들여쓰기 한다.
  3. .build() 이후 닫는 괄호 )와 세미콜론 ;는 한 줄로 작성한다. 즉, .build() 한 줄 띄고 );를 작성한다.
  4. 최종적으로 );는 마지막 줄에 정렬하여 닫는다.
        
### DTO 컨벤션 예시
-  메서드가 없는 경우
  ```java
  public record UserDto(
      String name,
      String email,
      int age,
      String address
  ) {}
  ```
- 메서드가 있는 경우
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

### Domain-Specific Guidelines
- Use domain aggregates for complex business logic (follow Order aggregate pattern)
- Implement value objects for type safety (Money, IDs)
- Place business rules in domain entities, not services
- Use domain events for cross-domain communication
- Repository interfaces go in domain layer, implementations in infrastructure

### Testing
- Domain entities have comprehensive unit tests
- Test state transitions and business rules thoroughly
- Use meaningful test data that reflects real business scenarios

## Environment Configuration

The application uses environment variables for configuration:
- Database: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- Redis: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- OAuth2: `GOOGLE_OAUTH2_CLIENT_ID`, `GOOGLE_OAUTH2_CLIENT_SECRET`
- JWT: `JWT_SECRET_KEY`, `JWT_ACCESS_TOKEN_EXPIRATION`
- Toss Payments: `TOSS_SECRET_KEY`, `TOSS_CLIENT_KEY`
