package base.testExample;

import base.framework.dependency.DIEngine;
import base.framework.dependency.DependencyContainer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        DependencyContainer dependencyContainer = new DependencyContainer();
        dependencyContainer.initialize();

        DIEngine diEngine = new DIEngine();
        diEngine.initializee();

        System.out.println(diEngine.getInstances());
//        MyController myController = diEngine.injectDependencies(MyController.class);
//        System.out.println(myController.handleGetRequest());
    }
}