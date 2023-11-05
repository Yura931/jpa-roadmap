package study.datajpa.entity;

import study.datajpa.dto.UserDto;
import study.datajpa.dto.UserRole;

public class MemberFactory {
    public static Member joinAdminMember(UserDto.JoinMemberCommand command) {
        return Member.builder()
                .username(command.getName())
                .age(2)
                .role(UserRole.ADMIN)
                .build();
    }

    public static Member joinNormalMember(UserDto.JoinMemberCommand command) {
        return Member.builder()
                .username(command.getName())
                .age(2)
                .role(UserRole.NORMAL)
                .build();
    }
}
