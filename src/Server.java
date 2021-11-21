import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
public class Server {

    public static ArrayList<BufferedWriter> m_OutputList; // 서버로 접속하는 클라이언트의 outputstream을 모아둔 배열
    public static ArrayList<LinkThread> threadList;

    public static long startTime;
    public static AtomicBoolean useable;
    public static writeLink write;

    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        m_OutputList = new ArrayList<BufferedWriter>();
        threadList = new ArrayList<LinkThread>();
        useable = new AtomicBoolean(true);
        write = new writeLink();
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


            threadList.get(0).join();
            write.writeAll();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String getTime() {
        return dateformat.format(System.currentTimeMillis() - startTime);
    }

}

class Linkfilestr implements Comparable<Linkfilestr> {

    int time;
    String str;
    int grade;

    public Linkfilestr(int time, String str, int grade) {
        this.time = time;
        this.str = str;
        this.grade = grade;
    }

    @Override
    public int compareTo(Linkfilestr o) {
        if (this.time > o.time)
            return 1;
        else if (this.time < o.time)
            return -1;

        //시간이 같을때
        else if(this.grade > o.grade)
            return 1;
        else if(this.grade < o.grade)
            return -1;

        return 0;

    }

    public String toString() {
        String t = new SimpleDateFormat("mm:ss:SSS").format(time);
        return t + str;
    }
}

class writeLink extends Thread {
    private PriorityQueue<Linkfilestr> queue;


    public writeLink() {
        queue = new PriorityQueue<>();

    }


    public void add(Linkfilestr str) {
        queue.add(str);
    }

    public void writeAll() {
        System.out.println("Link file 쓰는중");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Link.txt", true));
            while(!queue.isEmpty()) {
                bw.write(queue.poll().toString());
                bw.flush();
            }

            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}