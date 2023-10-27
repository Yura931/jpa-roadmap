package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회의 경우 readOnly = true로 해주면 JPA가 좀 더 최적화 시켜 줌, 조회가 많은 service의 경우 클래스 레벨에 readOnly = true를 해주고, 등록,수정 서비스에만 따로 Transactional 설정
// JPA의 모든 데이터변경이나 로직들은 가급적으로 transaction안에서 실행되게끔 해야 함, 그래야 LAZY로딩이나 이런 것들이 다 가능 한 것, springframework가 제공해주는 Transactional 어노테이션 사용 권장
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional  // 따로 설정해주는 경우 클래스 레벨보다 우선권 가짐
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();  // 영속화 이후 1차캐시에 pk로 값이 들어감
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 한명 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }


}
