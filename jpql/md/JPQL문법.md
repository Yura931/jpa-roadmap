# JPQL문법

```text
select_문 :: =
    select_절
    from_절
    [where_절]
    [groupby_절]
    [having_절]
    [orderby_절]

update_문 :: = update_절 [where_절]
delete_문 :: = delete_ 절 [where_절]
```
- select m from Member as m where m.age > 18
- 엔티티와 속성은 대소문자 구분O (Member, age)
- JPQL 키워드는 대소문자 구분X (SELECT, FROM, where)
- 엔티티 이름 사용, 테이블 이름이 아님(Member)
- 별칭은 필수(m) (as는 생략 가능)

### 집합과 정렬
```text
    select
        COUNT(m),   // 회원 수
        SUM(m.age), // 나이 합
        AVG(m.age), // 평균 나이
        MAX(m.age), // 최대 나이
        MIN(m.age)  // 최소 나이
    from Member m
```
- GROUP BY, HAVIG
- ORDER BY

### TypeQuery, Query
- TypeQuery: 반환 타입이 명확할 때 사용
- Query: 반환 타입이 명확하지 않을 때 사용

### 결과 조회 API
- query.getResultList(): 결과가 하나 이상일 때, 리스트 반환
  - 결과가 없음면 빈 리스트 반환
- query.getSingleResult(): 결과가 정확히 나나, 단일 객체 반환
  - 결과가 없으면: javax.persistence.NoResultException
  - 둘 이상이면: javax.persistence.NonUniqueResultException
  - 정말 결과가 하나일 때만 사용
  - String Data JPA -> 결과가 없으면 null반환 혹은 Optional로 받음, Exception 터트리지 않음, Spring Data가 try catch 대신 해줌

### 파라미터 바인딩 방법
- 위치기반, 이름기반 있는데 이름기반 쓰자 위치기반은 나중에 수정하려고 중간에 넣으면 답 없다.

### 프로젝션
- select 절에 조회할 대상을 지정하는 것, 뭘 가져올 것이냐, sql과 다름
- 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)
- select m from Member m -> 엔티티 프로젝션
- select m.team from Member m -> 엔티티 프로젝션
- select m.address from Member m -> 임베디드 타입 프로젝션
- select m.username, m.age from Member m -> 스칼라 타입 프로젝션
- DISTINCT로 중복 제거

### 엔티티 프로젝션
- select 절의 모든 엔티티가 영속성으로 관리 됨
- `select m.team from Member m` -> (묵시적 조인) 이 쿼리의 경우 team이 member와 연관관계로 되어있어 join이 되어 select가 되는데 이렇게 짜기보단  
    `select t from Member m join m.team t` -> 이런식으로 join을 건다는 것을 알 수 있도록 짜 주어야 한다.

### 임베디드 타입 프로젝션
- `select a.address from Address a` -> 이렇게는 불가능 임베디드 타입이기 때문에 소속되어있는 엔티티에서만 꺼내 쓸수 있음

### 스칼라 타입 프로젝션 여러 값 조회
- SELECT m.username, m.age FROM Member m
- Query 타입으로 조회
- Object[] 타입으로 조회
- **new 명령어로 조회** 가장 권장하는 방법
  - 단순 값은 DTO로 바로 조회
  - SELECT new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m
  - 패키지 명을 포함한 전체 클래스 명 입력
  - 순서와 타입이 일치하는 생성자 필요

### 페이징 API
- JPA는 페이징을 다음 두 API로 추상화
- setFirstResult(int startPosition): 조회 시작 위치(0부터 시작)
- setMaxResults(int maxResult): 조회할 데이터 수

### 조인
- 내부 조인: SELECT m FROM Member m [INNER] JOIN m.team t
- 외부 조인: SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
- 세타 조인: select count(m) from Member m, Team t where m.username = t.name
- ON절을 활용한 조인(JPA 2.1부터 지원)
  - 조인 대상 필터링
  - 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
  - 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
    - JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'A'
    - SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID = t.id and t.name = 'A'
  - 회원의 이름과 팀의 이름이 같은 대상 외부조인
    - JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
    - SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name

