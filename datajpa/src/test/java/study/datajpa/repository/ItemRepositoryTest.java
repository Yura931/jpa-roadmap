package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() throws Exception {
        // given
        Item item = new Item("A");  // 직접 정의한 pk, persist가 호출이 안되고, merge로 넘어가 버림, DB에 값이 있다고 생각하고 작동, select문이 나가고 값이 없을 때 그 시점에 없다고 판단하고 insert 해 줌
        itemRepository.save(item);  // spring data jpa save method
        // when

        // then
    }

}