package base.testExample;

import base.framework.anotations.Autowired;
import base.framework.anotations.Qualifier;
import base.framework.anotations.Service;

@Service
@Qualifier("myServiceImpl")
public class MyServiceImpl implements MyService {

    @Autowired
    private MyComponent myComponent;

    public String process() {
        return "Processing...";
    }
}