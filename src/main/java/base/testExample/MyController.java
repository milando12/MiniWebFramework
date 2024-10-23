package base.testExample;

import base.framework.anotations.*;

@Controller
public class MyController {
    @Autowired(verbose = true)
    @Qualifier("myServiceImpl")
    private MyService myService;

    @Autowired
    private MyComponent myComponent;

    @Autowired
    private MySingleton mySingleton;

    @GET
    @Path("/test/get")
    public String handleGetRequest() {
        System.out.println("MyController handleGetRequest()");
        return myService.process();
    }

    @POST
    @Path("/test/post")
    public String handlePostRequest() {
        System.out.println("MyController handlePostRequest()");
        return "POST request handled from MyController handlePostRequest()";
    }

    @GET
    @Path("/test2/get")
    public String handleGetRequest2() {
        System.out.println("MyController handleGetRequest2()");
        return myService.process();
    }
}