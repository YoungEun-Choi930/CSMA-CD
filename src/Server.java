import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    public static ArrayList<BufferedWriter> m_OutputList; // 서버로 접속하는 클라이언트의 outputstream을 모아둔 배열
    private static ArrayList<LinkThread> threadList;

    public static long startTime;
    public static boolean useable;

    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        m_OutputList = new ArrayList<BufferedWriter>();
        threadList = new ArrayList<LinkThread>();
        useable = true;

        try {
            ServerSocket s_socket = new ServerSocket(9999);

            FileWriter fw = new FileWriter("Link.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);



            System.out.println("소켓 기다리는중");

            for (int i = 0; i < 4; i++) // 소켓을 1, 2, 3, 4 순으로 보내니까 받는것도 1, 2, 3, 4로 들어감
            {
                Socket c_socket = s_socket.accept();
                LinkThread c_thread = new LinkThread();
                c_thread.setSocket(c_socket);

                m_OutputList.add(new BufferedWriter(new OutputStreamWriter(c_socket.getOutputStream())));
                threadList.add(c_thread);

                if (i == 0) {
                    startTime = System.currentTimeMillis();


                    System.out.println(getTime() + " Link Start");
                    System.out.println(getTime() + " System Clock Start");

                    bw.write(getTime() + " Link Start\r\n");
                    bw.write(getTime() + " System Clock Start\r\n");
                    bw.flush();
                    bw.close();
                    fw.close();
                }

                c_thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTime() {
        return dateformat.format(System.currentTimeMillis() - startTime);
    }

}