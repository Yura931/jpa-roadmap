package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        // emf의 경우 애플리케이션 로딩 시점에 딱 하나만 만들어 두어야 함
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");// Persistence-unit name

        // 실제 DB에 저장하거나 하는 트랜잭션 단위, 고객이 들어와서 행위(어떤 상품을 장바구니에 담고)를 할 때, DB커넥션을 얻어서 쿼리를 날리고 종료되는 일관적인 단위를 할 때마다 엔티티 매니저라는 애를 꼭 만들어주어야 함
        EntityManager em = emf.createEntityManager();   // 쉽게 생각해서 커넥션을 하나 받았다 생각하면 됨

        // JPA는 트랜잭션이라는 단위가 엄청 중요
        // 모든 데이터를 변경하는 작업은 꼭 트랜잭션 안에서 해주어야 함
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Member member = new Member();
            member.setUSERNAME("C");
            member.setRoleType(RoleType.ADMIN);

            em.persist(member);
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 내부적으로 DB 커넥션을 물고 동작, 꼭 닫아주어야 함
        }

        emf.close();
    }
}
