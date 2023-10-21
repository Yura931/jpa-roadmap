package hellojpa;

import javax.persistence.*;

import java.util.Arrays;
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

            Team team = new Team();
            team.setName("TeamA");

            Team team2 = new Team();
            team2.setName("TeamB");

            Team team3 = new Team();
            team3.setName("TeamC");

            em.persist(team);   // pk값이 세팅되고 영속상태가 됨
            em.persist(team2);
            em.persist(team3);


            Member member = new Member();
            member.setUsername("member1");
            member.changeTeam(team);
//            member.setCreatedBy("kim");

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.changeTeam(team2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.changeTeam(team3);

            em.persist(member);
            em.persist(member2);
            em.persist(member3);

            em.flush();
            em.clear();

            Team findTeam = em.find(Team.class, team.getId());  // 1차 캐시
            System.out.println("findTeam = " + findTeam.getMembers());

            em.flush();
            em.clear();

            System.out.println("===========================================");

//            List<Member> resultList = em.createQuery("select m.id, m.team from Member as m", Member.class).getResultList();
            List<Member> resultList = em.createQuery("select m from Member as m", Member.class).getResultList();
            // 우선 select쿼리 그대로 실행 -> Member를 보니까 Team이 EAGER로 되어 있네? Team도 가져와야 하네?? -> Team Select문 실행


            Team team1 = resultList.get(0).getTeam();
            System.out.println("team1 = " + team1);
            System.out.println("name = " + team1.getName());

//            for (Member m : resultList) {
//                System.out.println("m.getTeam() = " + m.getTeam().getName());
//            }

//            List<Member> findMembers = findTeam.getMembers();
//
//            for (Member m : findMembers) {
//                System.out.println("m = " + m.getUsername());
//            }


            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 내부적으로 DB 커넥션을 물고 동작, 꼭 닫아주어야 함
        }

        emf.close();
    }
}
