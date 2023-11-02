# Spring Data JPA

- JPA를 편하게 사용하는 기능
- JPA를 잘 모르면 사용하기 어려움 

### 공통 인터페이스
- `package org.springframework.data.repository;`
- `package org.springframework.data.jpa.repository;`
- commons 공통 프로젝트 라이브러리가 있고, redis, mongo, jpa 전용 라이브러리가 따로 존재

### 주요 메서드
- `save(S)`: 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합
- `delete(T)`: 엔티티 하나를 삭제한다. 내부에서 `EntityManger.remove()`호출
- `findById(ID)`: 엔티티 하나를 조회한다. 내부에서 `EntityManger.find()`호출
- `getOne(ID)`: 엔티티를 프록시로 조회한다. 내부에서 `EntityManager.getReference()`호출
- `findAll(...)`: 모든 엔티티를 조회한다. 정렬(`Sort`)이나 페이징(`Pageable`)조건을 파라미터로 제공할 수 있다.

### 쿼리 메서드
- 도메인에 특화된 기능 어떻게 해결해야 하지?
- Spring Data API는 QueryMethod 기능 제공

#### 메서드 이름으로 쿼리 제공
- spring boot 2.7.17 기준 공식문서 참고 (https://docs.spring.io/spring-data/jpa/docs/2.7.17/reference/html/#jpa.query-methods)
- 조회: find...By, read...By, query...By, get...By
- Count: count...By 반환타입 `long`
- EXISTS: exists...By 반환타입 boolean
- 삭제: delete...By, remove...By 반환타입 long
- DISTINCT: findDistinct, findMEmberDistinctBy
- LIMIT: findFirst3, findFirst, findTop, findTop3

> [참고] : 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다. 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생한다.  
> 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점


#### @NamedQuery는 알아만 두자
- 애플리케이션 로딩 시점에 파싱을 해두기 때문에 이 시점에 버그, 오류를 잡을 수 있다는 장점이 있다.

#### @Query
- repository 메소드에 쿼리 정의
- 쿼리 메서드 기능이 정말 좋지만 메서드명이 너무 길어질 수 있기 때문에 그 경우 @Query를 사용해서 직접 jpql정의해서 사용하면 유용하다.
- 이름이 없는 네임드 쿼리, 이 기능도 애플리케이션 로딩 시점에 파싱을 해 두어 오류를 잡을 수 있다.
```text
@Query("select m from Member m where m.username = :username and m.age = :age")
List<Member> findUser(@Param("username") String username, @Param("age") int age);
```
- 값, DTO 조회하는 방법
  - new operation.. jpa가 제공해주는 문법
- 파라미터 바인딩
  - 위치기반: `select m from Member m where m.username = ?0`
  - 이름기반: `select m from Member m where m.username = :username`


### 반환타입
```text
List<Member> findListByUsername(String username);   // 컬렉션
    -> 반환 타입이 List인 경우 값이 없을 때 빈 배열 반환
Member findMemberByUsername(String username);   // 단건
    -> 반환 타입이 Entity, DTO 단건인 경우 값이 없을 때 null 반환, spring data는 Exception을 터트리지 않고 null로 반환시켜 줌
Optional<Member> findOptionalByUsername(String username); // 단건 Optional
    -> 데이터가 있을지 없을지 모르면 그냥 옵셔널 써! Optional.empty 반환
    -> 반환 결과가 2개 이상인 경우 NonUniqueResultException(IncorrectResultSizeDataAccessException)
무엇이든 가능
```

### 페이징과 정렬
- 잘 하시는 분들이 좋은 것을 만들어 두셨다..

#### 스프링 데이터 JPA 페이징과 정렬
- 표준화 시켜 둠!
- 페이징과 정렬 파라미터
  - `org.springframework.data.domain.Sort`: 정렬 기능
  - `org.springframework.data.domain.Pageable`: 페이징 기능(내부에 `Sort` 포함)
- 특별한 반환 타입
  - `org.springframework.data.domain.Page`: 추가 count 쿼리 결과를 포함하는 페이징
  - `org.springframework.data.domain.Slice`: 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)
  - `List`(자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
- Page의 경우 count 쿼리가 항상 함께 나가는데 select쿼리가 복잡해지면 count 쿼리도 함께 복잡해져 성능 이슈가 생길 수 있음
```text
쿼리가 복잡해지면 countQuery를 분리해주는 것이 좋다.
@Query(value = "select m from Member m left join m.team t",
        countQuery = "select count(m) from Member m")   
Page<Member> findByAge(int age, Pageable pageable);
```

### 벌크성 수정 쿼리
- 벌크형 업데이트 쿼리는 영속성 컨텍스트에 1차캐시 되는 것이 아닌 DB에 바로 쿼리가 날아가는 것, 1차캐시 값과 디비에서 가져오는 값이 맞지 않을 수 있다.
- 벌크연산 실행 후 항상 영속성 컨텍스트를 날려주어야 함
- 같은 트랜잭션 내에서 다른 로직이 실행 되면 꼬일 수 있음
- spring data jpa는 @Modifying 어노테이션을 지원해주고, 이 어노테이션의 속성중 clearAutomatically = true를 설정해주면 쿼리가 나간 후 클리어 과정을 자동으로 실행해 준다.
  - `@Modifying(clearAutomatically)`

### @EntityGraph
- 연관된 걸 데이트베이스에서 조인 후 한번에 가져오는 것(프록시 객체가 아닌 디비에서 바로 가져온 객체), 한 방 쿼리로 끝!
- jpql을 만들지 않고 페치조인을 사용할 수 있는 어노케이션
```text
    // jpql 직접 작성
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    
    // @EntityGraph 어노테이션 사용
    @Override
    @EntityGraph(attributePaths = { "team" })
    List<Member> findAll();
    
    // jpql작성하고 페치조인 할 객체만 @EntityGraph에 설정
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    
    // 쿼리메서드와 섞어서 사용 가능
    @EntityGraph(attributePaths = { "team" })
    List<Member> findEntityGraphByUsername(@Param("username") String username);
    
    // namedEntityGraph 사용
    @NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);
```
- 복잡한 쿼리는 jpql 페치 조인 적극 사용, 간단한 쿼리는 @EntityGraph 활용 해보자, 상황에 맞게 잘 사용 할 것

### JPA Hint & Lock
- JPA Hint
  - JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
```text
// 이 쿼리는 읽기전용이라는 의미, 내부적으로 스냅샷을 찍지 않고 최적화 해 둠, 성능 테스트 해보고 필요하면 적용
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
```
- JPA LOCK
  - `select for update` 데이터베이스 비관적 락
  - JPA 지원
  - 어노테이션을 사용해 편리하게 사용할 수 있다.
  - Lock에대해 따로 공부해 보자
  - 실시간 트래픽이 많은 서비스는 lock을 걸면 큰일 날 수도 ..
```text
@Lock(LockModeType.PESSIMISTIC_WRITE) // package javax.persistence;
List<Member> findLockByUsername(String username);
``` 

# 확장 기능
### 사용자 정의 리포지토리 구현
- 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
- 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
- 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면??
  - JPA 직접 사용(`EntityManager`)
  - 스프링 JDBC Template 사용
  - MyBatis 사용
  - 데이터베이스 커넥션 직접 사용 등등..
  - Querydsl 사용
```text
// 
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    ...
}

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
```
- 잘 못 나누면 MemberRepository의 몸집만 불어나는 꼴이 될 수도 있음
- 필요하다면 핵심비즈니스와 화면에 맞춘 쿼리를 분리하는 것이 좋다.

### Auditing
- 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
  - 등록일
  - 수정일
  - 등록자
  - 수정자
- 위 네가지가 없으면 운영할 때 너무 힘들다..
```text
순수 JPA 활용한 방법
@MappedSuperclass 어노테이션이 있는 클래스 상속받아서 공통속성으로 적용

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @PrePersist // persist 하기 전 발생하는 이벤트
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.updateDate = now;
    }

    @PreUpdate // update 하기 전
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}

public class Member extends JpaBaseEntity {
    ...
}

public class Team extends JpaBaseEntity {
    ...
}
```
- 스프링 데이터 JPA 사용 
- 설정
  - `@EnableJpaAuditing`: 스프링 부트 설정 클래스에 적용해야 함
  - `@EntityListeners(AuditingEntityListener.class)`: 엔티티에 적용
- 사용 어노테이션
  - @CreateDate
  - @LastModifiedDate
  - @CreatedBy
  - @LastModifiedBy
- 등록자, 수정자를 처리해주는 `AuditorAware` 스프링 빈 등록
```text
@EnableJpaAuditing  -> 스프링 부트 설정 클래스에 적용해야 함
@SpringBootApplication
public class DatajpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatajpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		// 실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음
		// 등록, 수정될 때마다 이 provider를 호출해서 createdBy, modifedBy 값을 채워 줌
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}

@EntityListeners(AuditingEntityListener.class) -> 엔티티에 적용
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity{

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

### 스프링 데이터 JPA 구현체
- SimpleJpaRepository
```text
// 빈 등록 뿐 아니라 스프링에서 쓸 수 있는 예외로 다 변경 해 줌, 하부 기술을 JDBC에서 JPA로 바꿔도 exception 처리하는 매커니즘이 동일, 서비스나 컨트롤러나 ..
// 하부 구현 기술을 바꿔도 기존 비즈니스 로직에 영향을 주지 않도록 설계 되어 있음
@Repository  
@Transactional(readOnly = true) -> 서비스 계층에 transaction을 받아서 실행되기도 하고 없으면 개별적으로 실행 됨
public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {

}
```
- 내부적으로 jpa 사용해서 구현 되어 있음
- 내부(리파지토리 계층에!)에 데이터 변경하는 메서드에 Transactional이 걸려 있음 
  - 스프링 데이터 JPA는 변경(등록, 수정, 삭제)메서드를 트랜잭션 처리
  - 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
  - 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
- `@Transactional(readOnly = true)`
  - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 `readOnly = true` 옵션을 사용하면 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음
  - 기본적으로 트랜잭션이 끝날 때 JPA 영속성 컨텍스트에 있는 정보들을 DB에 플러시를 함
- **매우 중요!!!**
  - `save()`메서드
    - 새로운 엔티티면 저장(`persist`)
    - 새로운 엔티티가 아니면 병합(`merge`): 단점 - 디비에 셀렉트 쿼리가 한 번 나간다. 가급적 쓰면 안된다. 데이터 변경은 변경감지를 통해서!
  - 새로운 엔티티를 판단하는 기본 전략
    - 식별자가 객체일 때 `null`로 판단
    - 식별자가 자바 기본 타입일 때 `0`으로 판단
    - `Persistable` 인터페이스를 구현해서 판단 로직 변경 가능
      - JPA 식별자 생성 전략이`@GenerateValue`면 `save()` 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작
      - JPA 식별자 생성 전략이 `@Id`만 사용한 직접 할당이면 이미 식별자 값이 있는 상태로 `save()`를 호출해서 `merge()`가 호출된다.
      - `merge()`는 비효율적, `Persistable`을 사용해 새로운 엔티티 확인 여부를 직접 구현하는 것이 효과적이다.
      - 등록시간(`@CreatedDate`)을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다.
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {    // isNew() 직접 구현
        return createDate == null;
    }
}
```

### Specifications(명세)
- 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원
- 미래엔 어떻게 될 지 잘 모르겠지만.. JPA Criteria는 좀.. 
- **실무에서는 JPA Criteria 대신 QueryDSL 사용하자**

### QueryByExample
- 도메인 객체를 조건으로 사용
- 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
- 단점
  - 조인은 가능하지만 내부 조인(INNER JOIN)만 가능, 외부 조인(LEFT JOIN) 안 됨
  - 중첩 제약조건 안 됨
    - `firstname = ?0 or (firstname = ?1 and lastname = ?2)`
- 아우터 조인이 하나라도 들어가게 되면 다 버리고 다시 짜야 됨

### Projections - query의 select절에 들어 갈 데이터
- 엔티티 대신에 DTO를 편리하게 조회할 때 사용
- 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶으면??
- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
- 프로젝션 대상이 ROOT가 아니면
  - LEFT OUTER JOIN 처리
  - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
- 프로젝션 대상이 root 엔티티면 유용하다.
- 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다.
- 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자.

### 네이티브 쿼리
- JDBC를 직접 쓰거나, JDBC 템플릿, MyBatis를 가지고 SQL을 직접 짜는 것들을 말함
- JPA가 네이티브 쿼리를 지원 함
- 가급적 사용하지 않는 것이 좋음, 어쩔 수 없을 때 사용
- 엔티티 전체를 조회하기 보다 DTO에 맞춰 가져오고 싶을 때 사용하는데 반환타입 지원이 많이 안 되서 한계가 있음
- **네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or MyBatis 권장**
- DTO를 뽑는데 좀 편하게 뽑을 수 있는 것, 네이티브 쿼리이면서 동적 쿼리 뽑을 수 있는 방법은 있음
```java
// projection 인터페이스 사용
public interface MemberProjection {
    Long getId();
    String getUsername();
    String getTeamName();
}
```