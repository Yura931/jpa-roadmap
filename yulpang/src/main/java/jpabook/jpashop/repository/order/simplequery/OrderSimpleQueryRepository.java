package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private EntityManager em;

    // DTO를 통해 직접 조회 쿼리를 받는 경우 DTO자체가 화면의 스펙이 되는 것
    // 화면에 종속적인 쿼리가 될 수 있기 때문에 일반 repository와 분리해서 관리하면 유지보수성이 올라간다.
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +  // Entity가 아닌 DTO로 받는 경우 JPA가 알 수 있도록 new 오퍼레이션 사용
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }

}
