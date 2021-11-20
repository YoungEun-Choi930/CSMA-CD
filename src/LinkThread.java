import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;

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
            FileWriter fw = new FileWriter("Link.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);

            nodeNum = in.read(); // 송신자 받아옴
            int nodelistnum = nodeNum - 1;

            int time;

            while (!m_socket.isClosed() || !isInterrupted()) {
                try {
                    int receiver = in.read(); // 수신자 받아옴

                        time = (int) (System.currentTimeMillis() - Server.startTime); // 현재 시간 측정
                        //  System.out.println(dateformat.format(time) + " Node " + nodeNum + " Send Request To Node " + receiver);
                        bw.write(dateformat.format(time) + " Node " + nodeNum + " Send Request To Node " + receiver + "\r\n");
                        bw.flush();

                        if (Server.useable == true) { // 사용가능하면 사용불가능하게 만들고
                            // sender,receiver,result,time 전송.
                            Server.useable = false;

                            bw.write(dateformat.format(time) + " Accept: Node " + nodeNum + " Data Send Request To Node " + receiver + "\r\n");
                            bw.flush();

                            Server.m_OutputList.get(nodelistnum).write(nodeNum);
                            Server.m_OutputList.get(nodelistnum).write(receiver);
                            Server.m_OutputList.get(nodelistnum).write(1);
                            Server.m_OutputList.get(nodelistnum).write(time);
                            Server.m_OutputList.get(nodelistnum).flush();

                            Server.m_OutputList.get(receiver - 1).write(nodeNum);
                            Server.m_OutputList.get(receiver - 1).write(receiver);
                            Server.m_OutputList.get(receiver - 1).write(1);
                            Server.m_OutputList.get(receiver - 1).write(time);
                            Server.m_OutputList.get(receiver - 1).flush();

                            sleep(5);

                            bw.write(dateformat.format(time + 5) + " Node " + nodeNum + " Send Finished To Node " + receiver + "\r\n");
                            bw.flush();

                            Server.useable = true;
                        } else { // 사용가능하면 사용불가능하게 만들고
                            // sender,receiver,result,time 전송.


                            bw.write(dateformat.format(time) + " Reject: Node " + nodeNum + " Data Send Request To Node "
                                    + receiver + "\r\n");
                            bw.flush();

                            Server.m_OutputList.get(nodelistnum).write(nodeNum);
                            Server.m_OutputList.get(nodelistnum).write(receiver);
                            Server.m_OutputList.get(nodelistnum).write(0);
                            Server.m_OutputList.get(nodelistnum).write(time);
                            Server.m_OutputList.get(nodelistnum).flush();

                        }



                } catch (SocketException e) {        //와일문 돌다가 소켓이 사라지면 종료
                    break;
                }


            }
            System.out.println(dateformat.format(System.currentTimeMillis() - Server.startTime) + "링크 종료");

        } catch (Exception e) {
//            e.printStackTrace();
            this.interrupt();
        }
    }

    public void setSocket(Socket _socket) {
        m_socket = _socket;
    }


}
