package base.framework.dependency;

import base.framework.anotations.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Dependency Container to store class implementations for interfaces
public class DependencyContainer {
    private static final Map<String, Class<?>> container = new HashMap<>();

    public void initialize() throws IOException, ClassNotFoundException {
        List<Class<?>> classes = ClassScanner.getClasses("base");

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Qualifier.class)) {
                register(clazz.getAnnotation(Qualifier.class).value(), clazz);
            }
        }
        System.out.println("Map of implementations" +container);
    }

    private static void register(String key, Class<?> implementation) {
        if (container.containsKey(key)) {
            throw new RuntimeException("Duplicate qualifier value: " + key);
        }
        container.put(key, implementation);
    }

    public static Class<?> getImplementation(String key) {
        if (!container.containsKey(key)) {
            throw new RuntimeException("No implementation found for qualifier: " + key);
        }
        return container.get(key);
    }
}
