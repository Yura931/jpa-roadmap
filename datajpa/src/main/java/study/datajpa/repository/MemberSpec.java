package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

// spring data jpa가 Criteria를 이쁘게 사용할 수 있도록 `Specification`을 구현할 수 있도록 해줌. 하지만 실무에서는 사용하기 너무 어렵다.
public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {
        return new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (!StringUtils.hasText(teamName)) {
                    return null;
                }
                Join<Member, Team> t = root.join("team", JoinType.INNER);// 회원과 조인
                return builder.equal(t.get("name"), teamName);
            }
        };
    }

    public static Specification<Member> username (final String username) {
        return (Specification<Member>) (root, query, builder) -> builder.equal(root.get("username"), username);
    }
}
