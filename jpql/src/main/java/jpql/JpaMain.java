package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

                Team team = new Team();
                team.setName("teamA");
                em.persist(team);

                Member member = new Member();
                member.setUsername("member1");
                member.setAge(10);
                em.persist(member);




                em.flush();
                em.clear();
                String query = "";
                // String query = "select m from Member m order by m.age desc";
                // String query = "select m from Member m, Team t where m.username = t.name";
                // query = "select m from Member m left join m.team t on t.name = 'teamA'";
            query = "select m from Member m left join Team t on m.username = t.name";
                List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            System.out.println("result = " + result.size());

            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}