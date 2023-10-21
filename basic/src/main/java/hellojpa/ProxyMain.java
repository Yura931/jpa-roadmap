package hellojpa;

import org.hibernate.Hibernate;
import org.hibernate.proxy.AbstractLazyInitializer;
import org.hibernate.proxy.ProxyConfiguration;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;

import javax.persistence.*;

public class ProxyMain {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            Team team = new Team();
            team.setName("teamA");
            member1.changeTeam(team);
            em.persist(team);


            em.flush();
            em.clear();

            Member m = em.find(Member.class, member1.getId());
            System.out.println("m = " + m.getTeam().getClass());
            System.out.println("m = " + m.getTeam().getName());

            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close(); // 내부적으로 DB 커넥션을 물고 동작, 꼭 닫아주어야 함
        }

        emf.close();
    }
}
