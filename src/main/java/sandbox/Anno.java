package sandbox;

import java.lang.reflect.Method;

public class Anno {
    /**
     * prints all methods of java.lang.Object that are deprecated, and since when
     */
    public static void printDeprecatedMethods() {

        for (Method method : Object.class.getDeclaredMethods()) {

            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                System.out.println(method + " has been deprecated since Java " + deprecated.since());
            }
        }
    }

    /**
     * @return percentage of java.lang.Object methods marked as @IntrinsicCandidate
     */
    public static double percentageOfIntrinsicCandidates() {
        // TODO
        return 0;
    }

    /**
     * Determines if all 3 conditions hold:
     * - type is an interface
     * - type is NOT annotated with @FunctionalInterface
     * - type contains exactly 1 abstract method
     */
    public static boolean missesFunctionalInterfaceAnnotation(Class<?> type) {
        // TODO
        return false;
    }
}
