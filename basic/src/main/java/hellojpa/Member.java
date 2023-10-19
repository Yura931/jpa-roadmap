package hellojpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

// jpa를 사용하는 애구나?! 인식
@Entity
@Table(name="MEMBER")
public class Member {
    // JPA에게 PK가 무엇인지 알려주어야 함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false) // unique제약조건을 컬럼레벨에서 주면 이름이 랜덤성으로 만들어짐
    private String USERNAME;
    private Integer age;
    @Enumerated(EnumType.STRING) // Enum 사용시 EnumType은 항상 String으로 사용, 기본값인 ORDINAL 사용 시 ENUM 클래스에 값을 추가했을 때 버그 발생
    private RoleType roleType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @Lob    // @Lob에 String타입이면 기본 clob으로 매핑 됨
    private String description;

    @Transient
    private int temp;
    public Member() {

    }

    public Member(Long id, String USERNAME, Integer age, RoleType roleType, Date createDate, Date lastModifiedDate, String description) {
        this.id = id;
        this.USERNAME = USERNAME;
        this.age = age;
        this.roleType = roleType;
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
