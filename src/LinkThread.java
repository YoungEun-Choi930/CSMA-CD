import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.PriorityQueue;

public class LinkThread extends Thread {

    private Socket m_socket;
    private int nodeNum;
    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));

            nodeNum = in.read(); // 송신자 받아옴
            int nodelistnum = nodeNum - 1;



            boolean result;
            int time;
            while (true) {
                int receiver = in.read(); // 수신자 받아옴
                // int time = in.read();

                synchronized (LinkThread.class) {
                    time = (int) (System.currentTimeMillis() - Server.startTime); // 현재 시간 측정

                    System.out.println(time + "-- " + nodeNum + "->" + receiver + "보냈음" + System.currentTimeMillis());
                    if (time + 5 >= 10000) {

                        break;
                    }


                    Server.write.add(new Linkfilestr(time, " Node " + nodeNum + " Send Request To Node " + receiver + "\r\n",1));
                    if (Server.useable.compareAndSet(true, false)) { // 사용가능하면 사용불가능하게 만들고

                        result = true;
                        System.out.println(time + "-- " + nodeNum + "->" + receiver + " : Accept " + System.currentTimeMillis());

                        // sender,receiver,result,time 전송.
                        Server.write.add(new Linkfilestr(time, " Accept: Node " + nodeNum + " Data Send Request To Node " + receiver + "\r\n",2));


                    } else {
                        result = false;
                        System.out.println(time + "-- " + nodeNum + "->" + receiver + " : Reject " + System.currentTimeMillis());
                        Server.write.add(new Linkfilestr(time, " Reject: Node " + nodeNum + " Data Send Request To Node " + receiver + "\r\n",2));

                        Server.m_OutputList.get(nodelistnum).write(nodeNum);
                        Server.m_OutputList.get(nodelistnum).write(receiver);
                        Server.m_OutputList.get(nodelistnum).write(0);
                        Server.m_OutputList.get(nodelistnum).flush();

                    }

                }

                if(result) {


                    sleep(3);

                    Server.m_OutputList.get(nodelistnum).write(nodeNum);
                    Server.m_OutputList.get(nodelistnum).write(receiver);
                    Server.m_OutputList.get(nodelistnum).write(1);
                    Server.m_OutputList.get(nodelistnum).flush();

                    Server.m_OutputList.get(receiver - 1).write(nodeNum);
                    Server.m_OutputList.get(receiver - 1).write(receiver);
                    Server.m_OutputList.get(receiver - 1).write(1);
                    Server.m_OutputList.get(receiver - 1).flush();

                    System.out.println((time + 5) + " Node " + nodeNum + " Send Finished To Node " + receiver + ", " + System.currentTimeMillis());

                    Server.write.add(new Linkfilestr(time + 5, " Node " + nodeNum + " Send Finished To Node " + receiver + "\r\n",3));
                    Server.useable.set(true);
                }

            }
            System.out.println("링크끝");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block

            System.out.println("inturrupted!");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setSocket(Socket _socket) {
        m_socket = _socket;
    }
}