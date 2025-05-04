package sandbox;

import java.util.*;

public class Party {
    public static void main(String[] args) {
        Party party = new Party();
        System.out.println(party.adultDomains());
    }

    private final List<Person> persons = new ArrayList<Person>();

    public Party() {
        persons.add(new Person("Anna", 18, "anna@nass.de"));
        persons.add(new Person("Bernd", 17, "bernd@bibel.de"));
        persons.add(new Person("Caro", 25, "caro@yahoo.de"));
        persons.add(new Person("Dora", 49, "dora@yahoo.de"));
        persons.add(new Person("Edgar", 20, "edgar@erdapfel.de"));
        persons.add(new Person("Fritz", 5, "fritz@email.de"));
    }

    public List adultDomains() {
        Set domains = new HashSet();
        for (Iterator it = persons.iterator(); it.hasNext(); ) {
            Person person = (Person) it.next();
            if (person.isAdult()) {
                domains.add(person.getEmail().getDomain());
            }
        }
        List list = new ArrayList(domains);
        Collections.sort(list);
        return list;
    }
}
