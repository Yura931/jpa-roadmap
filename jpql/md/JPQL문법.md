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


### 경로 표현식
- 명시적 조인: join 키워드 직접 사용
- 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생(내부 조인만 가능)
- 명시적 조인을 사용하자 그래야 쿼리 튜닝도 쉽고 관리하기 용이하다.
- 묵시적 내부 조인 절대 사용 금지, 연관관계 맺어져 있는 필드의 경우 경로를 타고타고타고 들어가서 계속 묵시적 조인을 해오기 때문에 절때 사용하지 말자.

### 경로 탐색을 사용한 묵시적 조인
- 항상 내부 조인
- 컬렉션은 경로 탐색의 긑, 명시적 조인을 통해 별칭을 어덩야 함
- 경로 탐색은 주로 SELECT, WHERE절에서 사용하지만 묵시적 조인으로 인해 SQL의 FROM(JOIN)절에 영향을 줌

### 페치 조인(fetch join)
- SQL 조인 종류X
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
- join fetch 명령어 사용
- 페치 조인 ::=[LEFT[OUTER]|INNER] JOIN FETCH 조인경로
- 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에)
- SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
- [JPQL]: `select m from Member m join fetch m.team`
- [SQL]: `SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID = T.ID`
- 즉시로딩으로 가져오는 것과 같아보이지만 내가 타이밍을 정할 수 있다. 지연로딩을 설정해도 항상 fetch join을 우선으로 두자


- 일대다 관계, 컬렉션 페치 조인
- DB에서 일대다 조인 시 데이터가 뻥튀기 될 수 있음, 조심해야 함!
- SQL의 DISTINCT는 중복된 결과를 제거하는 명령
- JPQL의 DISTINCT 2가지 기능 제공
  - SQL에 DISTINCT를 추가
  - 애플리케이션에서 엔티티 중복 제거
  - SQL레벨에서는 전체 쿼리결과가 완전 중복되는 것이 아니기 때문에 중복제거가 안됨
  - JPQL이 애플리케이션 레벨에서 추가로 중복 제거를 시도하고 같은 식별자를 가진 Team 엔티티를 제거해 줌
  - 하이버네이트6 부터는 DISTINCT 명령어를 사용하지 않아도 애플리케이션에서 중복 제거가 자동으로 적용 됨

### 페치 조인과 일반 조인의 차이
- 일반 조인 실행 시 연관 된 엔티티를 함께 조회하지 않음
  - select 절에 연관된 데이터 정보가 없음, SELECT절에 지정한 엔티티만 조회
- [JPQL]: `select t from Team t join t.members m where t.name = '팀A'`
- [SQL]: `SELECT T.* FROM TEAM T INNER JOIN MEMBER M ON T.ID = M.TEAM_ID WHERE T.NAME = '팀A'`

### 페치 조인의 특징과 한계
- **페치 조인 대상에는 별칭을 줄 수 없다.**
  - 하이버네이트는 가능하지만 가급적 사용X, 별칭 주지 않는 것이 관례
  - 페치조인은 기본적으로 나와 연관된 것 모두 가져오는 것
  - 별칭 준 후 추가 조작 하지 말 것
  - 잘 못 조작하게 되면 데이터가 누락 될 수도 있음
  - 연관관계를 여러번 거쳐 가져가는 경우에만 아주아주 가끔 사용
- 둘 이상의 컬렉션은 페치 조인 할 수 없다.
- 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
  - 일대일, 다대일 같은 단일 값 연관 필드들은 패치 조인해도 페이징 가능
  - 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)

> 실무에서 대부분의 성능 이슈는 N + 1에서 나옴
> 이 이슈는 fetch join으로 많이 해결이 가능

### 정리
- 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인 보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환하는 것이 효과적


### 다형성 쿼리
- 엔티티를 다형성으로 설계 한 경우 조회 대상을 특정 자식으로 한정할 수 있음
  - [JPQL]: `select i from Item i where type(i) IN (Book, Movie)`
  - [SQL]: `select i from i where i.DTYPE in ('B', 'M')`
- TREAT(JPA 2.1)
  - 자바의 타입 캐스팅과 유사
  - 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
  - FROM, WHERE, SELECT(하이버네이트 지원) 사용
  - [JPQL]: `select i from Item i where treat(i as Book).author = 'kim'`
  - [SQL]: `select i.* from Item i where i.DTYPE = 'B' and i.author = 'kim'`

### 엔티티 직접 사용
- 기본 키 값
  - JPQL에서 엔티티를 직접 사용하면 SQL에서 해당 엔티티의 기본 키 값을 사용
    - [JPQL]: 
      - `select count(m.id) from Member m` // 엔티티의 아이디를 사용
      - `select count(m) from Member m` // 엔티티를 직접 사용
      - [SQL]:(JPQL 둘 다 같은 SQL 실행)
      - `select sount(m.id) as cnt from Member m`
  - 엔티티를 파라미터로 전달하거나 식별자를 파라미터로 전달하거나 실행 되는 SQL은 같다.    
- 외래 키 값
```text
Team team = em.find(Team.class, 1L);
String qlString = "select m from Member m where m.team = :team";
List resultList = em.createQuery(qlString)
                    .setParameter("team", team)
                    .getResultList();
                    
String qlString = "select m from Member m where m.team.id = :teamId";
List resultList = em.createQuery(qlString)
                    .setParameter("teamId", teamId)
                    .getResultList();
                    
-- 실행 SQL
select m.* from Member m where m.team_id = ?
```

### Named 쿼리 -정적 쿼리
- @Entity에 미리 선언
- 미리 정의해서 이름을 부여해두고 사용하는 JPQL
- 정적 쿼리 (동적 쿼리는 안됨)
- 어노테이션, XML에 정의
- 애플리케이션 로딩 시점에 초기화 후 재사용 -> JPA나 Hibernate같은 애들이 로딩시점에 sql로 파싱해서 캐시로 가지고 있음!, JPQL은 결국 파싱되어 실행되어야 하기 때문에 메리트가 있음
- 애플리케이션 로딩 시점에 쿼리를 검증, 컴파일 시점 다음으로 에러잡기 좋은 시점
- spring data jpa에서 @Query()애노테이션을 사용해서 인터페이스 위에 바로 선언 가능, 이것이 바로 Named쿼리! 이름없는 Named쿼리라 부름
```text
@Entity
@NamedQuery(
    name = "Member.findByUsername",
    query = "select m from Member m where m.username = :username")
public class MEmber {

}

List<Member> resultList =
    em.createNamedQuery("Member.findByUsername", Member.class)
    .setParameter("username", "회원1")
    .getResultList();
```
- XML이 항상 우선권을 가짐
- 애플리케이션 운영환경에 따라 다른 XML을 배포할 수 있다.


### 벌크 연산
- PK를 딱 집어서 하는 것 이외 일반적으로 알고 있는 UPDATE, DELETE문이라 생각하면 됨
- 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
- JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행이 됨
  - 상품 갯수만큼 조회하고 반목문 돌면서 UPDATE하고.. 너무 많은 실행
- 쿼리 한번으로 여러테이블 로우 변경(엔티티)
```text
// FLUSH 자동 호출
int resultCount = em.createQuery("update Member m set m.age = 20")
        .executeUpdate();
System.out.println("resultCount = " + resultCount);

// executeUpdate()문으로 실행 된 쿼리는 DB에만 적용이 된 상태이기 때문에 
// em.find()로 멤버의 나이를 가져와도 영속화 되어 있지 않아 값을 가져오지 못함

em.clear(); -> 영속화 초기화 후 DB에서 다시 가져오도록 해주어야 함

Member findMember = em.find(Member.class, member1.getId());
System.out.println("findMember = " + findMember.getAge());
```

### 벌크 연산 주의
- 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
- 해결 방법
  - 벌크 연산을 먼저 실행
  - 벌크 연산 수행 후 영속성 컨텍스트 초기화(영속화 되어있던 객체와 DB에 벌크연산 된 데이터를 동기화 해주기 위함)