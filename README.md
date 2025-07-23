## 해당 브랜치는 이해를 돕기 위한 예시 코드 입니다.
- DDD 기반 패키지 구조 / 레이어드 아키텍처 기반 패키지 구조 모두 사용
- 바이브 코딩으로 생성함

### example 도메인
- 간단한 예시

### order 도메인 - DDD 기반 패키지 구조
- 각 도메인 별로 presentation, application, domain, infrastructure로 나뉜다.
  - domain 계층에는 model, repository, service, event 폴더가 있다.
    - model에는 entity, vo, enums, aggregate 폴더가 있다.
  - presentation 계층에는 보통 rest 폴더가 있고, 상황에 따라 event, scheduled 폴더도 있을 수 있다.
    - 각 폴더 안에는 dto 폴더가 있을 수 있다. dto 폴더 안에는 request와 response 폴더가 각각 있다.
  - application 계층에는 service, facade, mapper 폴더가 있다.
  - infrastructure 계층에는 persistence와 external 폴더가 있다.
    - persistence/jpa, persistence/redis 등으로 성격에 따라 저장소를 분리한다.

### user 도메인 - 레이어드 아키텍처 기반 패키지 구조
- 각 도메인 별로 기본적으로 controller, dto, service, entity, repository로 나뉜다.
  - dto 폴더에는 request와 response 폴더가 각각 있다.
- entity 계층에는 enum class를 저장하는 enums 폴더가 존재할 수 있다.
