package sandbox;

import java.lang.reflect.Method;

public class Anno {
    /**
     * prints those methods of java.lang.Object marked as @Deprecated,
     * and since when
     */
    public static void printDeprecatedMethodsOfObject() {

        for (Method method : Object.class.getDeclaredMethods()) {

            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                System.out.println(method + " has been deprecated since Java " + deprecated.since());
            }
        }
    }

    /**
     * prints those methods of java.util.List marked as @SafeVarargs
     */
    public static void printSafeVarargsMethodsOfList() {
        // TODO
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
