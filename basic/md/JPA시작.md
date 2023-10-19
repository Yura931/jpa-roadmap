# JPA 설정 - persistence.xml

### JPA 설정파일
- /META-INF/persistence.xml 위치
- persistence-unit name으로 이름 지정
- javax.persistence로 시작: JPA 표준 속성
- hibernate로 시작 : 하이버네이트 전용 속성

```xml
<persistence-unit name="hello">
    <properties>
        <!-- 필수 속성 -->
        <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
        <property name="javax.persistence.jdbc.user" value="sa"/>
        <property name="javax.persistence.jdbc.password" value="1234"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
        <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

        <!-- 옵션 -->
        <property name="hibernate.show_sql" value="true"/>
        <property name="hibernate.format_sql" value="true"/>
        <property name="hibernate.use_sql_comments" value="true"/>
        <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
    </properties>
</persistence-unit>
```

### JPA 특징
- 특정 데이터베이스에 종속되지 않는다.
- 각각의 데이터베이스가 제공하는 SQL 문법과 함수가 조금씩 다름
  - 가변문자 : MYSQL은 VARCHAR, Oracle은 VARCHAR2
  - 문자열을 자르는 함수 : SQL 표준은 SUBSTRIING(), Oracle은 SUBSTR()
  - 페이징 : MYSQL은 LIMIT, Oracle은 ROWNUM
- 방언 : SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능
```xml
<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
```
- 어떤 방언을 사용할 것인지 설정 가능

### JPA 구동방식
[![3.png](https://i.postimg.cc/P55ZYS8M/3.png)](https://postimg.cc/TpBpXq0y)
- Persistence 클래스가 persistence.xml 설정정보를 읽고 EntityManagerFactory를 만듦
- 무언가 필요할 때 Factory에서 EntityManager를 만들어 냄


### 객체와 테이블 매핑
```java
package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");
            em.persist(member);
        tx.commit();
        em.close();
        emf.close();
    }
}
```
- 결과
```text
Hibernate: 
    /* insert hellojpa.Member
        */ insert 
        into
            Member
            (name, id) 
        values
            (?, ?)
```
- 콘솔에 뜨는 옵션
```xml
<properties>
  <!-- 옵션 -->
  <property name="hibernate.show_sql" value="true"/>  <!--println으로 출력-->                                                        
  <property name="hibernate.format_sql" value="true"/>  <!--이쁘게 포맷팅 해주는 옵션-->
  <property name="hibernate.use_sql_comments" value="true"/> <!--이 쿼리가 왜 나온거야?-->
  <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
</properties>
```
- 쿼리를 직접 만들지 않아도 맵핑 정보를 보고 JPA가 넣어 줌
- 테이블명, 컬럼명은 관례를 따름


### 주의사항
- 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
- 엔티티 매니저는 쓰레드간에 공유X(사용하고 버려야 한다.)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**
  - JPA가 변경을 감지하고 update문을 날려 줌
```java
public class JpaMain {
    public static void main(String[] args) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA");
            em.persist(member);
            
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA"); // 변경 감지

            // Java Collection을 다루는 것처럼 설계되어 있음
            // persist를 하지 않아도 update쿼리가 실행 됨
            // JPA를 통해서 ENTITY를 가져오면 JPA가 관리를 함
            // JPA가 변경이 되었는지 안되었는지를 트랜잭션 커밋하는 시점에 다 체크 함
            // 변경을 감지하고 update쿼리를 만들어서 날리고 커밋을 함
          
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close(); 
        }

        emf.close();
    }
}
```

> 내가 원하는 데이터들을 최적화 해서 가져와야 하고 .. 어떻게 할 것이냐 JPA에서는 JPQL제공

### JPQL
- SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어
- SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- JPA 입장에서 JPQL은 테이블을 대상으로 쿼리를 짜는 것이 아닌 객체를 대상으로 쿼리를 짬
```text
List<Member> result = em.createQuery("select m from Member as m", Member.class)
    .setFirstResult(1)
    .setMaxResults(10)
    .getResultList();
```

- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
- 모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요

> - JPQL은 엔티티 객체를 대상으로 쿼리 
>   - 방언을 바꾸어도 JPQL코드를 변경할 필요가 없다.
>   - Query DSL이나 라이브러리와 같이 쓰면 자바로 모든 걸 다 코딩 할 수 있다.
> - SQL은 데이터베이스 테이블을 대상으로 쿼리 
