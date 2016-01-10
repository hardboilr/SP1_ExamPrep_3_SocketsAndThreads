package test;

import client.Client;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import server.Server;

public class ServerTest {

    public ServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null);
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Server.stopServer();
    }

    @Test
    public void testA() throws InterruptedException, IOException {
        Thread.sleep(1000);

        Client turnstile1 = new Client();
        Thread t1 = new Thread(turnstile1);
        t1.start();
        turnstile1.connect("localhost", 9090);
        turnstile1.send("t1");
        
        
        Client turnstile2 = new Client();
        Thread t2 = new Thread(turnstile1);
        t2.start();
        turnstile2.connect("localhost", 9090);
        turnstile2.send("t2");
        
        Client turnstile3 = new Client();
        Thread t3 = new Thread(turnstile1);
        t3.start();
        turnstile3.connect("localhost", 9090);
        turnstile3.send("t3");
        
        Client turnstile4 = new Client();
        Thread t4 = new Thread(turnstile1);
        t4.start();
        turnstile4.connect("localhost", 9090);
        turnstile4.send("t4");
        
        Client turnstile5 = new Client();
        Thread t5 = new Thread(turnstile1);
        t5.start();
        turnstile5.connect("localhost", 9090);
        turnstile5.send("t5");
        
        Thread.sleep(1000);
        
        turnstile1.send("10");
        turnstile2.send("10");
        turnstile3.send("10");
        turnstile4.send("10");
        turnstile5.send("10");
    }

}
