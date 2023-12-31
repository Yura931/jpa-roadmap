package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 메타데이터를 클래스 Object에 그대로 적는 걸 선호
// 객체를 보고 인덱스, 컬럼 크기 등을 바로 확인하고 코드를 짤 수 있음
// DB 컬럼 명칭은 언더스코어가 관례
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(length = 10)
    private String name;

    @Embedded
    private Address addres;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddres() {
        return addres;
    }

    public void setAddres(Address addres) {
        this.addres = addres;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
