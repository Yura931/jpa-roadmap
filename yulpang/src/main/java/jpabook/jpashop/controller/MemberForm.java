package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    // Entity는 최대한 순수하게 유지하고 화면을 위한 Form객체나 Dto를 따로 두는 것이 좋다.

    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
