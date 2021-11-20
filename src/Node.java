import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Node {

    public ReceiveThread rec_thread;
    public SendThread send_thread;
    public Socket c_socket;


    public Node(int num) {


        try {
            c_socket = new Socket("127.0.0.1", 9999);

            send_thread = new SendThread();
            send_thread.setSocket(c_socket);
            send_thread.setnodeNum(num);

            rec_thread = new ReceiveThread();
            rec_thread.setSocket(c_socket);
            rec_thread.setnodeNum(num, send_thread);

            System.out.println("Node "+ num+" 연결 완료");


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        send_thread.start();
        rec_thread.start();
    }

}

