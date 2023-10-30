package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllCriteria(new OrderSearch());   // StackOverflowError 무한루프에 빠짐
        // Entity를 직접 반환 했을 때 나타나는 문제점들 ...
        // StackOverflowError를 해결하기 위해 양방향 연관관계가 맺어져 있는 Entity의 필드를 jsonignore설정을 통해 한쪽을 끊어준다.
        // 하지만 fetchtype = lazy인 경우 jackson라이브러리가 proxy객체를 인식하지 못해 에러 발생
        // Hibernate5module을 사용해 에러도 잡고 FORCE_LAZY_LOADING설정으로 강제 LAZY타입을 로딩시킬 수 있지만 성능낭비가 굉장해진다.
        // FORCE_LAZY_LOADING을 설정하지 않고 반복문을 돌려 직접 프록시객체에서 lazy연관관계가 걸려있는 엔티티를 호출해 DB에서 강제 초기화 되도록 하는 방법도 있다.
        // 결론 -> Entity를 직접 노출시키는 것은 아주 나쁘다. 사용하지 말자. DTO로 변환해서 반환하자.

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        // ORDER 2개
        // LAZY : N + 1 -> 1 + 회원 N + 배송 + N -> 최악의 경우 SQL이 무려 총 5번이나.., 지연로딩은 DB를 찌르는것이 아닌 영속성 컨텍스트를 찌르는 것 이미 조회된 경우 쿼리 생략
        // EAGER : 예측이 잘 안됨.. 맨 처음 jqpl을 sql로 그대로 변환 order 읽어온 후 연관관계를 찾아서 delivery member 가져와야되네?.. 하면서 order delivery member가 마구잡이로 조인이 되어 가져와짐. 사용하지 말자
        return orderRepository.findAllCriteria(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        // fetch join 사용
        // DB에서 조인걸어서 한 번 조회하고 끝!
        // LAZY 초기화 하지 않음
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        // select절에 내가 필요한 컬럼만 가져올 수 있음
        // Entity로 받는 경우 select절에 데이터를 DB에서 더 많이 퍼올림
        return orderSimpleQueryRepository.findOrderDtos();
    }

    // v3, v4는 우열을 가리기 힘들다.
    // v3 : 외부의 모습을 건들지 않고 내부의 성능만 fetch조인, 재사용성, 공용사용 가능
    // v4 : 실제 sql 짜듯이, 재사용성이 좀 없음 DTO에 종속, SELECT절에서 원하는 데이터를 직접 셀렉트 하기 때문에 애플리케이션 네트워크 용량 최적화(생각보다 미비), API스펙이 repository에 들어와 있게 되는 것, 성능최적화 된 쿼리용을 별도로 두고
    // api의 트래픽에 따라 유동적으로 잘 선택해서 사용하자
    // 셀렉트 절에 컬럼이 늘어나는 것 만으로는 성능에 많은 영향을 주지는 않음

    // repository는 가급적 순수한 Entity를 조회하는 데에만 사용
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화, 이 시점에 DB에서 조회 해오는 것
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
