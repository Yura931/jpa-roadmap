# querydsl

### QType
- `QMember member = new QMember("m")`: alias 직접 설정, 같은 테이블끼리 조인해야 되는 경우 사용 
- `QMember.member`: 만들어져 있는 인스턴스 사용

### JPQL이 제공하는 모든 검색 조건 제공
```java
member.username.eq("member1"); // username = 'member1'
member.username.ne("member1"); // username != 'member1'
member.username.eq("member1").not(); // username != 'member1'

member.username.isNotNull(); // 이름이 is not null

member.age.in(10, 20); // age in (10, 20)
member.age.notIn(10, 20); // age not in (10, 20)
member.age.between(10, 30); // between 10, 30

member.age.goe(30); // age >= 30
member.age.gt(30); // age > 30
member.age.loe(30); // age <= 30
member.age.lt(30); // age < 30

member.username.like("member%"); // like 검색
member.username.contains("member") // like '%member%' 검색
member.username.startWith("member") // like 'member%' 검색
```

### 결과 조회
- `fetch()`: 리스트 조회, 데이터 없으면 빈 리스트 반환
- `fetchOne()`: 단 건 조회
  - 결과가 없으면: `null`
  - 결과가 둘 이상이면: `com.querydsl.core.NonUniqueResultException`
- `fetchFirst()`: `limit(1).fetchOne()`
- ~~`fetchResults()`: 페이징 정보 포함, total count 쿼리 추가 실행~~ - Deprecated
- ~~`fetchCount()`: count 쿼리로 변경해서 count 수 조회~~ - Deprecated
  - `fetchResults(), fetchCount()`는 개발자가 작성한 select 쿼리를 기반으로 count용 쿼리를 내부에서 만들어서 실행 함, 이 기능은 select 구문을 단순히 count 처리하는 용도로 바꾸는 정도이다. 단순한 쿼리에서는 잘 동작하지만, 복잡한 쿼리에서는 제대로 동작하지 않음
  - count쿼리가 필요하다면 별도로 작성해야 함 
```text
@Test
public void count() {
Long totalCount = queryFactory
    // .select(Wildcard.count) // select count(*)
    .select(member.count()) // select count(member.id)
    .from(member)
    .fetchOne(); 
}
```

### 조인
- querydsl 서브쿼리의 경우 from절에서는 지원되지 않음
- 해결 방안
  - 서브쿼리를 join으로 변경
  - 애플리케이션에서 쿼리를 2번 분리해서 실행
  - nativeSQL을 사용 - 프롬절에 죽어도 서브쿼리를 써야해 ..
  - jpa의 한계
- 프롬절에 서브 쿼리를 쓰는 굉장히 많은 이유가 있는게 그 중 안 좋은 이유가 많음
- 쿼리에서 기능을 많이 제공하니까 SQL에 화면관 관련된 로직들도 넣고, 이쁘게 보여주기 위한 기능도 넣고
- 이러다보면 어쩔 수 없이 프롬절안에 프롬절.. 들어갈 수 밖에 없음
- SQL은 데이터 가져오는데 집중하고 필요하면 중간에 애플리케이션에서 로직을 태우고
- 화면에서 렌더링 될 이쁜 데이터 포맷 등등 이런 것들은 다 화면에서 해야 한다.
- 이렇게 하는 것이 DB 쿼리도 재활용 성이 생김
- 화면에 맞춰서 쿼리를 짜려하니 프롬절이 늘어날 수 밖에 없다.

### Case문
- DB는 최소한의 필터링과 그루핑을 해서 데이터를 줄이는 일만 하자
- 애플리케이션, 프레젠테이션 로직은 각각 단계에서 하자

## 중급 문법
### 프로젝션과 결과 반환 - 기본
> 프로젝션: select 대상 지정

#### 순수 JPA에서 DTO 조회 코드
```text
    @Test
    public void findDtoByJPQL() throws Exception {
        List<MemberDto> resultList = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();
        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }
```

#### querydsl 조회
- Property 접근 방법 (setter)
```text
    @Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
```

### 동적 쿼리
- BooleanBuilder
- Where 다중 쿼리

### 수정, 삭제 배치 쿼리, 벌크 연산

### 실무 활용 - 순수 JPA와 Querydsl


### 스프링 데이터 JPA가 제공하는 Querydsl 기능
> 실무에서 사용하기에는 조금 부족한 기능들
---

- **인터페이스 QuerydslPredicateExecutor**
  - 한계점
    - 조인X(묵시적 조인은 가능하지만 left join이 불가능하다.)
    - 클라이언트가 Querydsl에 의존해야 한다. 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다.
    - 복잡한 실무환경에서 사용하기에는 한계가 명확하다.
  - `QuerydslPredicateExecutor`는 Pageable, Sort를 모두 지원하고 정상 동작한다.


- **Querydsl Web지원**
  - Predicate를 컨트롤러계층에서 파라미터로 바로 받을 수 있다.
  - 한계점
    - eq, like, contains, in 단순한 조건만 가능
    - 조건을 커스텀하는 기능이 복잡하고 명시적이지 않음
    - 컨트롤러가 Querydsl에 의존
    - 복잡한 실무환경에서 사용하기에는 한계가 명확


- **리포지토리 지원 - QuerydslRepositorySupport**
  - 장점
    - `getQuerydsl().applyPagination()`스프링 데이터가 제공하는 페이징을 Querydsl로 편리하게 변환 가능(단! Sort는 오류발생)
    - `from()`으로 시작 가능(최근에는 QueryFactory를 사용해서 `select()`로 시작하는 것이 더 명시적)
    - EntityManager 제공
  - 한계
    - Querydsl 3.x 버전을 대상으로 만듦
    - Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
      - select로 시작할 수 없음(from으로 시작해야함)
    - `QueryFactory`를 제공하지 않음
    - 스프링 데이터 Sort 기능이 정상 동작하지 않음

