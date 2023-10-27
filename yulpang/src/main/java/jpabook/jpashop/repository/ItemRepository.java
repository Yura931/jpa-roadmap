package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {

        if(item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
            // 병합 사용 시 모든 속성이 변경 됨
            // 병합 시 값이 없으면 null로 업데이트 할 위험도 있음
            // 변경 감지로 업데이트 시에는 변경하고 싶은 속성을 선택 할 수 있음
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
