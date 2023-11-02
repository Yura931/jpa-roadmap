package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap@318beab4, 인터페이스만 만들면 실제 구현체는 spring data jpa가 가짜 객체 만들어 줌

    @Value("#{target.username + ' ' + target.age}") // spring spl 문법, open projection
    String getUsername();
}
