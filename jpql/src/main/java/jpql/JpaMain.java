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
            member.setType(MemberType.ADMIN);
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            Member member2 = new Member();



            em.flush();
            em.clear();
            String query = "";
                // String query = "select m from Member m order by m.age desc";
                // String query = "select m from Member m, Team t where m.username = t.name";
                // query = "select m from Member m left join m.team t on t.name = 'teamA'";
            query = "select (select avg(m1.age) From Member m1) as avgAge, (select sum(m1.age) From Member m1) as sumAge from Member m left join Team t on m.username = t.name";
            List<Object[]> result = em.createQuery(query)
                .getResultList();

            em.flush();
            em.clear();

            query = "select m.username, 'HELLO', TRUE from Member m " +
                            "where m.type = :userType " +
                            "and m.age >= 10 " +
                            "and m.username like '%kim%'";

            query = "select " +
                    "case when m.age <= 10 then '학생요금' " +
                    "   when m.age >= 60 then '경로요금' " +
                    "   else '일반요금' " +
                    "end " +
                    "from Member m";

            query = "select nullif(m.username, '이름 없는 회원') from Member m ";

            query = "select function('group_concat', m.username) From Member m";
//            query = "select group_concat(m.username) From Member m";    // hibernate query 지원 문법

            query = "select m.username, m.team from Member m";   // username의 경우 상태 필드로 더이상 탐색할 경로가 없다. / 연관관계가 걸려있는 team(단일 값 연관 경로)의 경우 탐색이 더 가능하고, 묵시적 내부 조인이 발생한다. 조심해서 사용해야겠구나
            query = "select m.username From Team t join t.members m";     // 컬렉션 값 연관 경로, 묵시적 내부 조인이 발생한다. 탐색 더 안 됨, FROM 절에서 명시적 조인 사용해야 탐색 가능

            List<Object[]> result2 = em.createQuery(query)
//                    .setParameter("userType", MemberType.ADMIN) // 패키지명을 포함해서 쿼리에 넣지 않고 setParameter() 메서드에 세팅해주어도 된다.
                    .getResultList();

            for (Object objects : result2) {
                System.out.println("objects = " + objects);
            }

            em.flush();
            em.clear();

            query = "select " +
                    "case when m.age <= 10 then '학생요금' " +
                    "   when m.age >= 60 then '경로요금' " +
                    "   else '일반요금' " +
                    "end " +
                    "from Member m";

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
