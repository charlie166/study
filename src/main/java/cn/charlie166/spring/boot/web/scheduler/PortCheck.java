package cn.charlie166.spring.boot.web.scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PortCheck {

    public static boolean checkConnect(String hostname, int port){
        int timeout = 20000;
        SocketAddress sa = new InetSocketAddress(hostname, port);
        Socket socket = new Socket();
        try {
            socket.connect(sa, timeout);
            return true;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return false;
    }

    public static void check(){
        String hostname = "home.charlie166.club";
        int startPort = 10443;
        while(startPort < 65535) {
            System.out.println(String.format("result[%s:%s]:%s", hostname, startPort, PortCheck.checkConnect(hostname, startPort)));
            startPort += 1000;
        }
    }

    public static void main(String[] args) {
        String hostname = "locvps.charlie166.xyz";
        int port = 34561;
        System.out.println(String.format("result[%s:%s]:%s", hostname, port, PortCheck.checkConnect(hostname, port)));
        PortCheck.check();
    }
}
