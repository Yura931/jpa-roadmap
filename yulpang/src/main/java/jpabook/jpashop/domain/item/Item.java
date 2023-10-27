package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.InheritanceType.*;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // == 비즈니스 로직 == //
    // 도메인 주도 개발
    // 데이터를 가지고 있는 쪽에 비즈니스 로직이 있는 것이 좋음. 응집력이 있다.
    // 변경 해야하는 경우 핵심 비즈니스를 가지고 변경, 가장 객체 지향적
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
       int restStock = this.stockQuantity - quantity;
       if(restStock < 0) {
           throw new NotEnoughStockException("need more stock");
       }
       this.stockQuantity = restStock;
    }

    public void change(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this. stockQuantity = stockQuantity;
    }
}
