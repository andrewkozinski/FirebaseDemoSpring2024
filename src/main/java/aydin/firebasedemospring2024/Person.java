package aydin.firebasedemospring2024;

public class Person {
    private String name;
    private int age;
    private String phone;

    public Person(String name, int age, String phonenumber) {
        this.name = name;
        this.age = age;
        this.phone = phonenumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
