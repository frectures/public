package sandbox;

import java.math.BigInteger;
import java.util.stream.Stream;

public class Fibo {
    // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, ...
    public static Stream<BigInteger> fibonacci() {

        record Pair(BigInteger a, BigInteger b) {
        }

        return Stream.iterate(
                new Pair(BigInteger.ZERO, BigInteger.ONE),

                old -> new Pair(old.b(), old.a().add(old.b()))
        ).map(Pair::a);
    }

    public static void main(String[] args) {
        fibonacci().limit(12).forEach(System.out::println);
    }
}
