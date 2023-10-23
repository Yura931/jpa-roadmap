package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;

public class ValueMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Address address = new Address("homeCity", "street1", "10000"); // 수정자를 닫아놨기 때문에 address의 값을 변경하려면 새로 객체를 만들어 세팅을 해주어야 한다, 객체참조는 항상 불변객체를 통해서!

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(address);
            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("피자");
            member.getFavoriteFoods().add("족발");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========================== START ================================");
            Member findMember = em.find(Member.class, member.getId());
            List<AddressEntity> addressHistory = findMember.getAddressHistory();
            for (AddressEntity ad : addressHistory) {
                System.out.println("ad = " + ad);
            }

            Set<String> favoriteFood = findMember.getFavoriteFoods();
            for (String food : favoriteFood) {
                System.out.println("food = " + food);
            }

            Address a = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

            // 치킨 -> 한식
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            // 값 타입이기 때문에 수정작업을 할 수 없음 갈아끼우는 작업만 가능

            findMember.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));  // 기본적으로 equals로 object를 찾음, equals를 제대로 ovrride 해 주어야 함
            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street", "10000"));  // 기본적으로 equals로 object를 찾음, equals를 제대로 ovrride 해 주어야 함


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
