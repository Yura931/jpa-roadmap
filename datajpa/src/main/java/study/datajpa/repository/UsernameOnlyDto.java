package study.datajpa.repository;

public class UsernameOnlyDto {

    // Dto 객체를 직접 만들어서 사용
    private final String username;

    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
