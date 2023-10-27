//package jpabook.jpashop;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class MemberRepositoryTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Test
//    @Transactional  // EntityManager를 통한 모든 데이터 변경은 항상 한 트랜잭션 안에서 이루어져야 함, spring에 종속되어 있으니 springframework가 제공해주는 것 사용, test에 있는경우 DB rollback 해 줌
//    @Rollback(value = false) // 자동 롤백 되지 않도록 설정
//    public void testMember() throws Exception {
//        // given
//        Member member = new Member();
//        member.setUsername("memberA");
//
//        // when
//        Long saveId = memberRepository.save(member);
//        Member findMember = memberRepository.find(saveId);
//
//        // then
//        assertThat(findMember.getId()).isEqualTo(member.getId());
//        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//        assertThat(findMember).isEqualTo(member);
//    }
//}