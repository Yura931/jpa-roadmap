package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FetchJoinMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();
            String query = "";
//            query = "select m from Member m";
            query = "select m from Member m join fetch m.team"; // fetch 한번에 가져와
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());

                // fetch join 사용 안 했을 때
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차캐시)
                // 회원3, 팀B(SQL)

                // 회원 100명 -> 최악의 경우 쿼리가 100번.. N + 1, 1은 첫번째 회원을 가져오기 위해 날린 쿼리, 즉시로딩 지연로딩 다 발생

                // fetch join으로 해결
                // join으로 한방 쿼리가 돌고 query를 날리고 저장하는 시점에 이미 영속화되어 있음 프록시가 아님
                // 지연로딩 없이 깔끔하게 가능
                // 조회 시 많이 사용
            }

            query = "select distinct t From Team t join fetch t.members";
            List<Team> teamList = em.createQuery(query, Team.class)
                    .getResultList();

            for (Team team : teamList) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers().size());

/*
                team = teamA|members = 2
                team = teamA|members = 2
                team = teamB|members = 1

                DB의 일대다 관계의 경우 결과 뻥튀기
                어쩔 수 없음 가져와지는대로 가져와서 사용해야 함
*/
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }

            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
