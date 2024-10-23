package base.testExample;

import base.framework.anotations.Autowired;
import base.framework.anotations.Bean;

@Bean(scope = "singleton")
public class MySingleton {
    @Autowired
    private MyComponent myComponent;
}
