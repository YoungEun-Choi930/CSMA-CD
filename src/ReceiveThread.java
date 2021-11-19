import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ReceiveThread extends Thread {

    private Socket m_Socket;
    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");
    private static int rejectCount;

    private int nodeNum;
    private SendThread send_thread;

    @Override
    public void run() { // 어떤 노드가 나한테 보냈을 때
        // TODO Auto-generated method stub
        super.run();
        rejectCount = 0;

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));

            int sender;
            int receiver;
            int result;
            int time;

            BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", false));
            bw.write("00:00:00 Node "+ nodeNum+" Start" + "\r\n");
            bw.flush();
            bw.close();

            while (true) {
                sender = in.read();
                receiver = in.read();
                result = in.read();
                time = in.read();
                System.out.println(sender+" "+receiver+" "+result+" "+time +"센더 리시버 리절트 타임");
                if(sender == 60000) {
                    System.out.println("시간초과!");
                    System.out.println("Receive "+nodeNum+"- Socket 연결이 종료되었습니다.");
                    send_thread.interrupt();
                    break;
                }

                if (receiver == nodeNum) {

                    bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true)); // 추가
                    bw.write(dateformat.format(time) + " Data Receive Start from Node " + sender + "\r\n");
                    bw.flush(); // 추가

                    if (result == 1) {
                        bw.write(dateformat.format(time + 5) + " Data Receive Finished from Node " + sender + "\r\n");
                        bw.flush(); // 추가

                    } else {
                        bw.write(dateformat.format(time) + " Data Receive Rejected from Node " + sender + "\r\n");
                        bw.flush(); // 추가
                    }
                    bw.close();
                    receiver = 10;
                }

                else if (sender == nodeNum) {

                    bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true)); // 추가
                    bw.write(time+") "+ dateformat.format(time) + " Data Send Request To Node " + receiver + "\r\n");
                    bw.flush(); // 추가

                    if (result == 1) {
                        bw.write(dateformat.format(time + 5) + " Data Send Finished To " + receiver + "\r\n");
                        bw.flush(); // 추가
                    } else {
                        int back = BackoffTimer(rejectCount++);

                        bw.write(dateformat.format(time) + " Data Send Request Reject from Link" + "\r\n");
                        bw.flush(); // 추가

                        bw.write(dateformat.format(time) + " Exponentail Back-off Time: "+back+" msec\r\n");
                        bw.flush(); // 추가

                    }
                    bw.close();
                }

            }

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            //   } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            //    this.interrupt();
        }
        //
    }

    private int BackoffTimer(int transNum) {
        int rndom;
        int temp;
        temp = Math.min(transNum, 10);
        rndom = (int) (Math.random() * (Math.pow(2, temp) - 1));

        send_thread.Backoff(rndom);
        return rndom;
    }

    public void setSocket(Socket _socket) {
        m_Socket = _socket;
    }

    public void setnodeNum(int num, SendThread send) {
        nodeNum = num;
        send_thread = send;
    }

}