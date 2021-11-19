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
    public static AtomicBoolean useable;

    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        m_OutputList = new ArrayList<BufferedWriter>();
        threadList = new ArrayList<LinkThread>();
        useable = new AtomicBoolean(true);

        try {
            ServerSocket s_socket = new ServerSocket(9999);

            FileWriter fw = new FileWriter("Link.txt", false);
            BufferedWriter bw = new BufferedWriter(fw);

            Thread timer_thread = new Thread(() -> { // 1분을 세어줄 쓰레드. 1분 후에 LinkThread들 inturrupt

                try {
                    Thread.sleep(10000);
                    for (int i = 0; i < 4; i++) {
                        threadList.get(i).interrupt();
                    }

                    s_socket.close();
                    FileWriter fw2 = new FileWriter("Link.txt", true);
                    BufferedWriter bw2 = new BufferedWriter(fw2);

                    System.out.println("01:00:000 System Clock Finished");
                    System.out.println("01:00:000 Link Finished");
                    bw2.write("01:00:000 System Clock Finished\r\n");
                    bw2.write("01:00:000 Link Finished\r\n");
                    bw2.flush();
                    bw2.close();
                    fw2.close();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

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
                    timer_thread.start();

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String getTime() {
        return dateformat.format(System.currentTimeMillis() - startTime);
    }

}