package study.datajpa.dto;

import lombok.*;
import study.datajpa.entity.Member;

import javax.validation.constraints.NotNull;

public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class JoinMemberRequest {
        @NotNull
        private String email;
        @NotNull
        private String name;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class JoinMemberCommand {
        private final String email;
        private final String name;

        public static JoinMemberCommand of(String email, String name) {
            return new JoinMemberCommand(email, name);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JoinMemberResponse { // 단순 프로세스 결과, 조회 결과 담으면 끝. 어플리케이션 내에서 꺼내 쓸 일 없음. getter는 쓸일 없으면 안 여는게 맞음
        private final String name;
        private final Integer code;

        public static JoinMemberResponse memberEntityToResponse(Member save) {
            return new JoinMemberResponse(save.getUsername(), 200);
        }
    }
}
