package study.datajpa.repository;

public interface NestedClosedProjections {
    // 중첩 구조에서 첫번째에 있는 루트는 최적화가 되지만 두번째부터는 최적화가 안 됨 엔티티 그대로 불러 옴
    // 조인은 left join을 하게 됨
    // 조인이 들어가는 순간 사용하기 애매 해 짐
    /*
    select
        member0_.username as col_0_0_,
        team1_.team_id as col_1_0_,
        team1_.team_id as team_id1_2_,
        team1_.create_date as create_d2_2_,
        team1_.last_modified_date as last_mod3_2_,
        team1_.created_by as created_4_2_,
        team1_.last_modified_by as last_mod5_2_,
        team1_.name as name6_2_
    from
        member member0_
    left outer join
        team team1_
            on member0_.team_id=team1_.team_id
    where
        member0_.username=?
    */
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
