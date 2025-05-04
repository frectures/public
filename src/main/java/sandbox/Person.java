package sandbox;

public class Person {
    private final String name;
    private final int age;
    private final Email email;

    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = new Email(email);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isAdult() {
        return age >= 18;
    }

    public Email getEmail() {
        return email;
    }
}
