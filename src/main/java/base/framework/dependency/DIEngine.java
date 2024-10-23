package base.framework.dependency;

import base.framework.anotations.*;
import base.framework.request.Request;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DIEngine {
    // Singletons(Controllers, Services, Bean(singleton))
    private final static Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    // Initialize the dependency container and instances
    public void initializee() throws IOException, ClassNotFoundException {
        List<Class<?>> classes = ClassScanner.getClasses("base");

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                instances.computeIfAbsent(clazz, c -> {
                    try {
                        return injectDependencies(c);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException("Failed to initialize dependency: " + c.getName(), e);
                    }
                });
            } /*else if (clazz.isAnnotationPresent(Service.class) ||
                    (clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope().equals("singleton"))) {
                instances.computeIfAbsent(clazz, this::initializeDependency);
            }*/
        }
    }

    // Entry point to initiate dependency injection
    public <T> T injectDependencies(Class<T> controllerClass) throws IllegalAccessException, InstantiationException {
        T instance = controllerClass.newInstance();
        injectFields(instance);
        return instance;
    }

    // Recursively inject dependencies using reflection
    private void injectFields(Object instance) throws IllegalAccessException, InstantiationException {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                // This can be an interface, already initialized singleton or Component
                field.setAccessible(true);
                Object dependency = getDependencyInstance(field.getType(), field.getAnnotation(Qualifier.class));
                field.set(instance, dependency);
                if (field.getAnnotation(Autowired.class).verbose()) {
                    System.out.println("Initialized " + field.getType() + " " + field.getName() +
                            " in " + instance.getClass().getName() +
                            " on " + System.currentTimeMillis() +
                            " with " + dependency.hashCode());
                }
                field.setAccessible(false);
            }
        }
    }

    // Get the dependency instance either from the container or create a new one
    private Object getDependencyInstance(Class<?> type, Qualifier qualifier) throws IllegalAccessException, InstantiationException {
        if (type.isInterface()) {
            Class<?> implementation = DependencyContainer.getImplementation(qualifier.value());
            return instances.computeIfAbsent(implementation, k -> initializeDependency(implementation));
        } else if (type.isAnnotationPresent(Bean.class) && type.getAnnotation(Bean.class).scope().equals("singleton") ||
                type.isAnnotationPresent(Service.class)) {
            return instances.computeIfAbsent(type, this::initializeDependency);
        } else {
            return initializeDependency(type);
        }
    }

    // Create a new instance of a class and inject its dependencies
    private Object initializeDependency(Class<?> type) {
        try {
            Object instance = type.newInstance();
            injectFields(instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to initialize dependency: " + type.getName(), e);
        }
    }

    public static Object processRequest(Request request) throws IllegalAccessException, InstantiationException {
        for (Map.Entry<Class<?>, Object> entry : instances.entrySet()) {
            Class<?> controllerClass = entry.getKey();
            Object controllerInstance = entry.getValue();

            // Check if the controller has the right path and method
            if (controllerClass.isAnnotationPresent(Controller.class)) {
                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (isMethodMatch(method, request.getMethod()) && isPathMatch(method, request.getLocation())) {
                        // Invoke the matched method on the controller instance
                        try {
                            return method.invoke(controllerInstance);
//                            return;  // Assuming only one method should match; remove if multiple methods can match
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException("Failed to invoke method: " + method.getName(), e);
                        }
                    }
                }
            }
        }

        // If no matching method is found
        throw new RuntimeException("No matching method found for request: " + request.getMethod() + " " + request.getLocation());
    }

    private static boolean isMethodMatch(Method method, base.framework.request.enums.Method requestMethod) {
        return method.isAnnotationPresent(GET.class) && requestMethod == base.framework.request.enums.Method.GET ||
                method.isAnnotationPresent(POST.class) && requestMethod == base.framework.request.enums.Method.POST;
    }

    private static boolean isPathMatch(Method method, String requestLocation) {
        return method.isAnnotationPresent(Path.class) &&
                method.getAnnotation(Path.class).value().equals(requestLocation);
    }

    //TODO test delete later
    public Map<Class<?>, Object> getInstances() {
        return instances;
    }
}
