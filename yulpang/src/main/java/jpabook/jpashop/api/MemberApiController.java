package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    // request, response 역할을 하는 객체를 따로 생성
    // 별도의 DTO를 사용하면 api 스펙이 바뀌지 않음
    // Entity를 파라미터로 그대로 받는것은 좋지 않음, 실무에서는 절때 금지

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();// array를 바로 반환하면 json 스펙이 굳어버림. 유연성이 없어 짐
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(MemberDto::of)
                .collect(Collectors.toList());

        return new Result(collect.size(), collect); // Result로 한번 감싸서 반환
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
//    @AllArgsConstructor
    static class MemberDto {
        private String name;
        private Address address;

        private MemberDto(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public static MemberDto of(Member member) {
            return new MemberDto(member.getName(), member.getAddress());
        }
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);// update는 순수 변경감지만 하도록 하고 return을 위한 객체는 쿼리를 다시 짜는 방식으로 커맨드와 쿼리 분리
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }




    @Data
    @RequiredArgsConstructor
    static class MembersResponse {
        private Long id;
        private String name;
        private Address address;
    }
    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {

        @NotEmpty(message = "name은 필수 값 입니다.")
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
