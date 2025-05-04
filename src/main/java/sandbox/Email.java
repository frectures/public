package sandbox;

class Email implements Comparable<Email> {
    private final String email;

    public Email(String email) {
        this.email = email;
    }

    public String getDomain() {
        System.out.println(email + "-Domain wurde abgefragt");
        return email.substring(email.lastIndexOf('@') + 1);
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public int compareTo(Email that) {
        return this.email.compareTo(that.email);
    }
}
