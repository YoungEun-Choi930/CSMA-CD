import java.io.*;
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
                    bw.write(getTime() + " Link Start\r\n"); // 일단 파일에 먼저 작성해서 콘솔 I/O로 시간 늘어나는거 최소화
                    bw.write(getTime() + " System Clock Start\r\n");
                    bw.flush();
                    bw.close();
                    fw.close();

                    System.out.println(getTime() + " Link Start"); // 콘솔에서는 링크의 시작과 끝만 출력
                    System.out.println(getTime() + " System Clock Start");
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

    private static String getTime() { return dateformat.format(System.currentTimeMillis() - startTime); }
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

    public int compareTo(Linkfilestr o) {
        if (this.time > o.time)
            return 1;
        else if (this.time < o.time)
            return -1;

        else if(this.grade > o.grade) //시간이 같을때
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

    public writeLink() { queue = new PriorityQueue<>(); }

    public void add(Linkfilestr str) { queue.add(str); }

    public void writeAll() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Link.txt", true));
            while(!queue.isEmpty()) {
                bw.write(queue.poll().toString());
                bw.flush();
            }
            System.out.println("1:00:000 System Clock Finished");
            System.out.println("1:00:000 Link Finished");
            bw.write("01:00:000 System Clock Finished\r\n");
            bw.write("01:00:000 Link Finished\r\n"); // 링크 끝난 것 출력
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class LinkThread extends Thread {

    private Socket m_socket;
    private int nodeNum;
    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");

    @Override
    public void run() {

        super.run();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
            nodeNum = in.read(); // 송신자 받아옴

            int nodelistnum = nodeNum - 1;
            boolean result;
            int time;

            while (true) {
                int receiver = in.read(); // 수신자 받아옴

                synchronized (LinkThread.class) {
                    time = (int) (System.currentTimeMillis() - Server.startTime); // 현재 시간 측정

                    if (time + 5 >= 60000) { break; }

                    Server.write.add(new Linkfilestr(time, " Node " + nodeNum + " Send Request To Node " + receiver + "\r\n",1));

                    if (Server.useable.compareAndSet(true, false)) { // 사용가능하면 사용불가능하게 만들고
                        result = true;
                        Server.write.add(new Linkfilestr(time, " Accept: Node " + nodeNum + " Data Send Request To Node " + receiver + "\r\n",2));
                    } else {
                        result = false;
                        Server.write.add(new Linkfilestr(time, " Reject: Node " + nodeNum + " Data Send Request To Node " + receiver + "\r\n",2));

                        Server.m_OutputList.get(nodelistnum).write(nodeNum);
                        Server.m_OutputList.get(nodelistnum).write(receiver);
                        Server.m_OutputList.get(nodelistnum).write(0);
                        Server.m_OutputList.get(nodelistnum).flush();
                    }
                }

                if(result) {
                    sleep(4);
                    time = (int) (System.currentTimeMillis() - Server.startTime);
                    Server.write.add(new Linkfilestr(time, " Node " + nodeNum + " Send Finished To Node " + receiver + "\r\n",3));
                    Server.useable.set(true);

                    Server.m_OutputList.get(nodelistnum).write(nodeNum);
                    Server.m_OutputList.get(nodelistnum).write(receiver);
                    Server.m_OutputList.get(nodelistnum).write(1);
                    Server.m_OutputList.get(nodelistnum).flush();

                    Server.m_OutputList.get(receiver - 1).write(nodeNum);
                    Server.m_OutputList.get(receiver - 1).write(receiver);
                    Server.m_OutputList.get(receiver - 1).write(1);
                    Server.m_OutputList.get(receiver - 1).flush();
                }
            }
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void setSocket(Socket _socket) { m_socket = _socket; }
}