package base.framework.dependency;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassScanner {
    public static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {

//        System.out.println("Scanning package: " + packageName);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
//            System.out.println("Scanning resource: " + resource);
            classes.addAll(findClasses(resource, packageName));
        }

        return classes;
    }

    private static List<Class<?>> findClasses(URL directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try {
            File directoryFile = new File(directory.toURI());

            if (directoryFile.exists()) {
//                System.out.println("Scanning directory file: " + directoryFile);
                File[] files = directoryFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            classes.addAll(findClasses(file.toURI().toURL(), packageName + "." + file.getName()));
                        } else if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            classes.add(Class.forName(className));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }
        return classes;
    }

}
