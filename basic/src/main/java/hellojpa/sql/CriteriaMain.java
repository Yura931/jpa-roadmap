package hellojpa.sql;

import hellojpa.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CriteriaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            // Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);

            CriteriaQuery<Member> cq = query.select(m);

            String username = "asdf";
            if(username != null) {
                cq = cq.where(cb.equal(m.get("username"), "kim")); // 심플한 SQL은 괜찮지만 조금 복잡한 쿼리는 알아보기 힘들 수 있음, SQL 직접 짜는것보다 직관성이 좀 떨어질 수 있음, SQL스럽지 않다.
                                                                        // 오타를 내도 컴파일 시점에 잡아주는 장점이있고 동적쿼리도 훨씬 깔끔하게 나오는 장점도 있음
                                                                        // 실무에서는 잘 안쓴다???! 알아보기 생각보다 많이 어려움, 약간 망한 스펙...
            }

            List<Member> resultList = em.createQuery(cq).getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
