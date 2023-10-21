package jpabook.jpashop;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

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
        try {
            // 양방향 예시를 위한 설계일 뿐 실전에서 객체 양방향은 많이 고민해 볼 필요가 있다.

            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");
            em.persist(book);
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 내부적으로 DB 커넥션을 물고 동작, 꼭 닫아주어야 함
        }

        emf.close();
    }
}
