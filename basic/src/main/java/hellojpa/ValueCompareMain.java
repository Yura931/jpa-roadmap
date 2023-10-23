package hellojpa;

public class ValueCompareMain {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;

        System.out.println("(a==b): " + (a==b)); // true, 내부에 값이 들어있음

        Address address1 = new Address("city", "street", "10000");
        Address address2 = new Address("city", "street", "10000");

        System.out.println("address1 == address2: " + (address1 == address2)); // false 참조값을 비교하기 때문에 당연히 false
        System.out.println("address1.equals(address2) = " + address1.equals(address2)); // true override한 equals 사용

    }
}
