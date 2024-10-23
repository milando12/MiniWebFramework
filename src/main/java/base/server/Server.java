package base.server;

import base.framework.dependency.DIEngine;
import base.framework.dependency.DependencyContainer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DependencyContainer dependencyContainer = new DependencyContainer();
        dependencyContainer.initialize();

        DIEngine diEngine = new DIEngine();
        diEngine.initializee();

        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
